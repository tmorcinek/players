package com.morcinek.players.ui.teams.event


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.eventForTeamLiveDataForValueListener
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.core.itemCallback
import com.morcinek.players.core.ui.showDeleteCodeConfirmationDialog
import com.morcinek.players.ui.lazyNavController
import com.morcinek.recyclerview.list
import kotlinx.android.synthetic.main.fragment_event_details.*
import kotlinx.android.synthetic.main.vh_text.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class EventDetailsFragment : BaseFragment(R.layout.fragment_event_details) {

    private val viewModel by viewModelWithFragment<EventDetailsViewModel>()

    private val navController by lazyNavController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = moveTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            observe(viewModel.event) {
                title.text = it.type
                year.text = it.getDate().toDayOfWeekDateFormat()
                status.apply {
                    setText(it.statusText())
                    setTextColor(resources.getColor(it.statusColor()))
                }
            }
            recyclerView.list<PlayerData>(itemCallback()) {
                resId(R.layout.vh_text)
                onBind { _, item -> name.text = item.toString() }
                liveData(viewLifecycleOwner, viewModel.players)
            }
        }
    }

    override val menuConfiguration = createMenuConfiguration {
        addAction(R.string.action_delete, R.drawable.ic_delete) {
            showDeleteCodeConfirmationDialog(
                R.string.delete_event_query,
                R.string.delete_event_message
            ) { viewModel.deleteEvent { navController.popBackStack() } }
        }
        addAction(R.string.action_edit, R.drawable.ic_edit) {
            navController.navigate(R.id.nav_edit_event, bundle {
                putString(viewModel.teamKey)
                putParcel(viewModel.event.value!!)
            })
        }
    }
}

class EventDetailsViewModel(val references: FirebaseReferences, val teamKey: String, eventData: EventData) : ViewModel() {

    val event = references.eventForTeamLiveDataForValueListener(teamKey, eventData.key)
    val players = references.playersForTeamLiveDataForValueListener(teamKey).combineWith(event) { players, event ->
        players.filter { it.key in event.players }
    }

    fun deleteEvent(doOnComplete: () -> Unit) =
        references.teamEventReference(teamKey, event.value!!.key).removeValue().addOnCompleteListener { doOnComplete() }
}

private fun EventData.statusText() = if (optional) R.string.optional else R.string.mandatory
private fun EventData.statusColor() = if (optional) R.color.colorPrimary else R.color.colorAccent

val eventDetailsModule = module {
    viewModel { (fragment: Fragment) -> EventDetailsViewModel(get(), fragment.getString(), fragment.getParcelable()) }
}
