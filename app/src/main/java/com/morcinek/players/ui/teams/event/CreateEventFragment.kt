package com.morcinek.players.ui.teams.event

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.observe
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.showDatePickerDialog
import com.morcinek.players.core.extensions.toStandardString
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.core.itemCallback
import com.morcinek.players.core.selectableListAdapter
import com.morcinek.players.core.ui.showStandardDropDown
import com.morcinek.players.ui.lazyNavController
import com.morcinek.players.ui.teams.addPlayers.updateSelectedItem
import kotlinx.android.synthetic.main.fragment_create_event.*
import kotlinx.android.synthetic.main.header_button.view.*
import kotlinx.android.synthetic.main.vh_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*

class CreateEventFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_create_event

    private val viewModel by viewModelWithFragment<TeamDetailsViewModel>()

    private val navController: NavController by lazyNavController()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            typeLayout.header.setText(R.string.type)
            typeLayout.value.setText(R.string.value_not_set)
            typeLayout.apply {
                header.text = "TYPEX"
            }
            typeLayout.setOnClickListener {
                it.showStandardDropDown(android.R.layout.simple_dropdown_item_1line, listOf("Training", "Game", "Tournament", "Friendly")) {
                    viewModel.updateValue { type = it }
                    typeLayout.value.text = it
                }
            }
            dateLayout.header.setText(R.string.date)
            viewModel.event.value?.let { event ->
                dateLayout.value.text = event.getDate().toStandardString()
                dateLayout.setOnClickListener {
                    showDatePickerDialog(event.getDate()) {
                        viewModel.updateValue { dateInMillis = it.timeInMillis }
                        dateLayout.value.text = it.toStandardString()
                    }
                }
            }
            recyclerView.apply {
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.adapter = selectableListAdapter<PlayerData>(R.layout.vh_selectable_player, itemCallback()) { item, view ->
                    view.name.text = item.toString()
                }.apply {
                    observe(viewModel.players) { submitList(it) }
                    observe(viewModel.selectedPlayers) { selectedItems = it }
                    onClickListener { _, item -> viewModel.select(item) }
                }
            }
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

class TeamDetailsViewModel(private val references: FirebaseReferences, private val teamData: TeamData) : ViewModel() {

    val event: LiveData<EventData> = MutableLiveData<EventData>().apply { value = EventData().apply { setDate(Calendar.getInstance()) } }

    val players = references.playersForTeamLiveDataForValueListener(teamData.key)

    val selectedPlayers: LiveData<Set<PlayerData>> = MutableLiveData<Set<PlayerData>>().apply { value = setOf() }

    val isNextEnabled: LiveData<Boolean> = Transformations.map(selectedPlayers) { it.isNotEmpty() }

    fun select(player: PlayerData) = (selectedPlayers as MutableLiveData).apply {
        postValue(updateSelectedItem(player))
    }

    fun updateValue(function: EventData.() -> Unit) {
        (event as MutableLiveData).postValue(event.value?.apply(function))
    }

    fun addPlayersToTeam(doOnComplete: () -> Unit) {
        val value = event.value!!.apply { players = selectedPlayers.value!!.map { it.key } }
        references.teamEventsReference(teamData.key).push().setValue(value).addOnCompleteListener { doOnComplete() }
    }
}