package com.morcinek.players.ui.teams.event

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.map
import com.morcinek.players.core.database.observe
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.toDayOfWeekDateFormat
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.core.itemCallback
import com.morcinek.players.core.simpleListAdapter
import kotlinx.android.synthetic.main.fragment_event_details.*
import kotlinx.android.synthetic.main.vh_text.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class EventDetailsFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_event_details

    private val viewModel by viewModelWithFragment<EventDetailsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            title.text = viewModel.eventData.type
            year.text = viewModel.eventData.getDate().toDayOfWeekDateFormat()
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
}

val eventDetailsModule = module {
    viewModel { (fragment: Fragment) -> EventDetailsViewModel(get(), fragment.getParcelable(), fragment.getParcelable()) }
}

class EventDetailsViewModel(references: FirebaseReferences, teamData: TeamData, val eventData: EventData) : ViewModel() {

    val players = references.playersForTeamLiveDataForValueListener(teamData.key).map { it.filter { it.key in eventData.players } }
}
