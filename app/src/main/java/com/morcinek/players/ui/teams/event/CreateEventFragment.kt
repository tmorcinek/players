package com.morcinek.players.ui.teams.event

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.SelectListAdapter
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.*
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.showDatePickerDialog
import com.morcinek.players.core.extensions.toStandardString
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.core.itemCallback
import com.morcinek.players.core.ui.showStandardDropDown
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_create_event.*
import kotlinx.android.synthetic.main.header_button.view.*
import kotlinx.android.synthetic.main.vh_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*

class CreateEventFragment : BaseFragment(R.layout.fragment_create_event) {

    private val viewModel by viewModelWithFragment<TeamDetailsViewModel>()

    private val navController: NavController by lazyNavController()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            typeLayout.apply {
                header.setText(R.string.type)
                value.setText(R.string.value_not_set)
                setOnClickListener {
                    it.showStandardDropDown(android.R.layout.simple_dropdown_item_1line, listOf("Training", "Game", "Tournament", "Friendly")) {
                        viewModel.updateValue { type = it }
                        value.text = it
                    }
                }
            }
            dateLayout.apply {
                header.setText(R.string.date)
                viewModel.event.let { event ->
                    value.text = event.getDate().toStandardString()
                    setOnClickListener {
                        showDatePickerDialog(event.getDate()) {
                            viewModel.updateValue { dateInMillis = it.timeInMillis }
                            value.text = it.toStandardString()
                        }
                    }
                }
            }
            mandatorySwitch.setOnCheckedChangeListener { _, isChecked -> viewModel.updateValue { optional = !isChecked } }
            recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = SelectListAdapter<PlayerData>(R.layout.vh_selectable_player, itemCallback()) { _, item ->
                    name.text = item.toString()
                }.apply {
                    observe(viewModel.players) { submitList(it) }
                    viewModel.selectedPlayers = selectedItems
                }
            }
            observe(viewModel.selectedPlayers) { selectedPlayersNumber.text = "${it.size} selected" }
            nextButton.apply {
                viewModel.isNextEnabled.observe(this@CreateEventFragment) { isEnabled = it }
                setOnClickListener {
                    viewModel.addPlayersToTeam { navController.popBackStack() }
                }
            }
        }
    }

}

val createEventModule = module {
    viewModel { (fragment: Fragment) -> TeamDetailsViewModel(get(), fragment.getParcelable()) }
}

private class TeamDetailsViewModel(private val references: FirebaseReferences, private val teamData: TeamData) : ViewModel() {

    private val eventData = mutableValueLiveData(EventData().apply { setDate(Calendar.getInstance()) })

    val event: EventData
        get() = eventData.value!!

    val players = references.playersForTeamLiveDataForValueListener(teamData.key)

    lateinit var selectedPlayers : LiveData<Set<PlayerData>>

    val isNextEnabled: LiveData<Boolean> by lazy {
        selectedPlayers.combineWith(eventData) { players, event -> players.isNotEmpty() && event.type.isNotEmpty() && event.dateInMillis > 0 }
    }

    fun updateValue(function: EventData.() -> Unit) {
        eventData.postValue(event.apply(function))
    }

    fun addPlayersToTeam(doOnComplete: () -> Unit) {
        event.apply { players = selectedPlayers.value!!.map { it.key } }
        references.teamEventsReference(teamData.key).push().setValue(event).addOnCompleteListener { doOnComplete() }
    }
}