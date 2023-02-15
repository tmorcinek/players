package com.morcinek.players.ui.teams.event

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morcinek.android.HasKey
import com.morcinek.android.itemCallback
import com.morcinek.android.list
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.core.ui.showStandardDropDown
import com.morcinek.players.databinding.FragmentCreateEventBinding
import com.morcinek.players.databinding.VhSelectablePlayerBinding
import com.morcinek.players.ui.lazyNavController
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*

class CreateEventFragment : BaseFragment<FragmentCreateEventBinding>(FragmentCreateEventBinding::inflate) {

    private val viewModel by viewModelWithFragment<CreateEventViewModel>()

    private val navController by lazyNavController()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            typeLayout.run {
                header.setText(R.string.type)
                value.text = viewModel.event.type.takeIf { it.isNotEmpty() } ?: getString(R.string.value_not_set)
                root.setOnClickListener {
                    it.showStandardDropDown(android.R.layout.simple_dropdown_item_1line, listOf("Training", "Game", "Tournament", "Friendly")) {
                        viewModel.updateValue { type = it }
                        value.text = it
                    }
                }
            }
            dateLayout.run {
                header.setText(R.string.date)
                viewModel.event.let { event ->
                    value.text = event.getDate().toStandardString()
                    root.setOnClickListener {
                        showDatePickerDialog(event.getDate()) {
                            viewModel.updateValue { dateInMillis = it.timeInMillis }
                            value.text = it.toStandardString()
                        }
                    }
                }
            }
            mandatorySwitch.setOnCheckedChangeListener { _, isChecked -> viewModel.updateValue { optional = !isChecked } }
            recyclerView.list(itemCallback<SelectedPlayer>(), VhSelectablePlayerBinding::inflate) {
                onBind { _, player ->
                    name.text = player.name
                    root.run {
                        isSelected = player.isSelected
                        setOnClickListener {
                            viewModel.updateValue { players = (if (player.key in players) players.minus(player.key) else players.plus(player.key)) }
                        }
                    }
                }
                liveData(viewLifecycleOwner, viewModel.selectedPlayers)
            }
            observe(viewModel.selectedPlayers) { selectedPlayersNumber.text = "${it.count { it.isSelected }} selected" }
            nextButton.run {
                viewModel.isNextEnabled.observe(this@CreateEventFragment) { isEnabled = it }
                setOnClickListener {
                    viewModel.createOrUpdateEvent { navController.popBackStack() }
                }
            }
        }
    }

}

private class CreateEventViewModel(private val references: FirebaseReferences, private val teamKey: String, editEvent: EventData? = null) : ViewModel() {

    val event: EventData
        get() = eventData.value!!

    private val players = references.playersForTeamLiveDataForValueListener(teamKey)

    val eventData = MutableLiveData(editEvent ?: EventData().apply { setDate(Calendar.getInstance()) })

    val selectedPlayers = combine(eventData, players) { event, players -> players.map { SelectedPlayer(it.toString(), it.key in event.players, it.key) } }

    val isNextEnabled = eventData.map { event -> event.players.isNotEmpty() && event.type.isNotEmpty() && event.dateInMillis > 0 }

    fun updateValue(function: EventData.() -> Unit) {
        eventData.postValue(event.apply(function))
    }

    fun createOrUpdateEvent(doOnComplete: () -> Unit) {
        if (event.key.isNotEmpty()) {
            references.teamEventsReference(teamKey).child(event.key).setValue(event).addOnCompleteListener { doOnComplete() }
        } else {
            references.teamEventsReference(teamKey).push().setValue(event).addOnCompleteListener { doOnComplete() }
        }
    }
}

private data class SelectedPlayer(val name: String, val isSelected: Boolean, override val key: String) : HasKey

val createEventModule = module {
    viewModel { (fragment: Fragment) -> CreateEventViewModel(get(), fragment.getString(), fragment.getParcelableOrNull()) }
}
