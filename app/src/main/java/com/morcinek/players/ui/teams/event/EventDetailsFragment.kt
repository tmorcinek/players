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
import com.morcinek.players.core.database.map
import com.morcinek.players.core.database.observe
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.alert.alert
import com.morcinek.players.core.extensions.alert.noButton
import com.morcinek.players.core.extensions.alert.yesButton
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.toDayOfWeekDateFormat
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.core.itemCallback
import com.morcinek.players.core.simpleListAdapter
import com.morcinek.players.core.ui.showCodeConfirmationDialog
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_event_details.*
import kotlinx.android.synthetic.main.vh_text.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class EventDetailsFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_event_details

    private val viewModel by viewModelWithFragment<EventDetailsViewModel>()

    private val navController: NavController by lazyNavController()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            title.text = viewModel.eventData.type
            year.text = viewModel.eventData.getDate().toDayOfWeekDateFormat()
            status.setText(viewModel.statusText)
            status.setTextColor(resources.getColor(viewModel.statusColor))
            recyclerView.apply {
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.adapter = simpleListAdapter<PlayerData>(R.layout.vh_text, itemCallback()) { _, item, view ->
                    view.name.text = item.toString()
                }.apply {
                    viewModel.players.observe(this@EventDetailsFragment) { submitList(it) }
                }
            }
        }
    }

    override val menuConfiguration = createMenuConfiguration(R.menu.delete) {
        addAction(R.id.delete) {
            alert(R.string.delete_event_query) {
                yesButton { showCodeConfirmationDialog(R.string.delete_event_message) { viewModel.deleteEvent { navController.popBackStack() } } }
                noButton {}
            }.show()
        }
    }

}

val eventDetailsModule = module {
    viewModel { (fragment: Fragment) -> EventDetailsViewModel(get(), fragment.getParcelable(), fragment.getParcelable()) }
}

class EventDetailsViewModel(val references: FirebaseReferences, val teamData: TeamData, val eventData: EventData) : ViewModel() {

    val players = references.playersForTeamLiveDataForValueListener(teamData.key).map { it.filter { it.key in eventData.players } }

    val statusText = if (eventData.optional) R.string.optional else R.string.mandatory
    val statusColor = if (eventData.optional) R.color.colorPrimary else R.color.colorAccent

    fun deleteEvent(doOnComplete: () -> Unit) =
        references.teamEventReference(teamData.key, eventData.key).removeValue().addOnCompleteListener { doOnComplete() }

}
