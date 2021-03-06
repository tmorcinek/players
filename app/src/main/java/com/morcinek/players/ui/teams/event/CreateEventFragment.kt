package com.morcinek.players.ui.teams.event

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.SelectionListAdapter
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.*
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

    private val navController by lazyNavController()

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
                adapter = SelectionListAdapter<PlayerData>(R.layout.vh_selectable_player, itemCallback()) { _, item ->
                    name.text = item.toString()
                }.apply {
                    selectedItems = viewModel.selectedPlayers.value!!
                    observe(viewModel.players) { submitList(it) }
                    onSelectedItemsChanged { viewModel.selectedPlayers.postValue(it) }
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

private class TeamDetailsViewModel(private val references: FirebaseReferences, private val teamData: TeamData) : ViewModel() {

    private val eventData = MutableLiveData(EventData().apply { setDate(Calendar.getInstance()) })

    val event: EventData
        get() = eventData.value!!

    val players = references.playersForTeamLiveDataForValueListener(teamData.key)

    val selectedPlayers = mutableSetValueLiveData<PlayerData>()

    val isNextEnabled = selectedPlayers.combineWith(eventData) { players, event -> players.isNotEmpty() && event.type.isNotEmpty() && event.dateInMillis > 0 }

    fun updateValue(function: EventData.() -> Unit) {
        eventData.postValue(event.apply(function))
    }

    fun addPlayersToTeam(doOnComplete: () -> Unit) {
        event.apply { players = selectedPlayers.value!!.map { it.key } }
        references.teamEventsReference(teamData.key).push().setValue(event).addOnCompleteListener { doOnComplete() }
    }
}

val createEventModule = module {
    viewModel { (fragment: Fragment) -> TeamDetailsViewModel(get(), fragment.getParcelable()) }
}
