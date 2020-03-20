package com.morcinek.players.ui.teams.event


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.extensions.map
import com.morcinek.players.core.extensions.observe
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.moveTransition
import com.morcinek.players.core.extensions.toDayOfWeekDateFormat
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.core.itemCallback
import com.morcinek.players.core.listAdapter
import com.morcinek.players.core.ui.showDeleteCodeConfirmationDialog
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_event_details.*
import kotlinx.android.synthetic.main.vh_text.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class EventDetailsFragment : BaseFragment(R.layout.fragment_event_details) {

    private val viewModel by viewModelWithFragment<EventDetailsViewModel>()

    private val navController: NavController by lazyNavController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = moveTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            title.text = viewModel.eventData.type
            year.text = viewModel.eventData.getDate().toDayOfWeekDateFormat()
            status.apply {
                setText(viewModel.statusText)
                setTextColor(resources.getColor(viewModel.statusColor))
            }
            recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = listAdapter<PlayerData>(R.layout.vh_text, itemCallback()) { _, item ->
                    name.text = item.toString()
                }.apply {
                    viewModel.players.observe(this@EventDetailsFragment) { submitList(it) }
                }
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
    }
}

class EventDetailsViewModel(val references: FirebaseReferences, val teamData: TeamData, val eventData: EventData) : ViewModel() {

    val players = references.playersForTeamLiveDataForValueListener(teamData.key).map { it.filter { it.key in eventData.players } }

    val statusText = if (eventData.optional) R.string.optional else R.string.mandatory
    val statusColor = if (eventData.optional) R.color.colorPrimary else R.color.colorAccent

    fun deleteEvent(doOnComplete: () -> Unit) =
        references.teamEventReference(teamData.key, eventData.key).removeValue().addOnCompleteListener { doOnComplete() }
}

val eventDetailsModule = module {
    viewModel { (fragment: Fragment) -> EventDetailsViewModel(get(), fragment.getParcelable(), fragment.getParcelable()) }
}
