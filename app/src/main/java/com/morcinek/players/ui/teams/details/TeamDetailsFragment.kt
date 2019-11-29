package com.morcinek.players.ui.teams.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.*
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.eventsForTeamLiveDataForValueListener
import com.morcinek.players.core.database.observe
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_team_details.view.*
import kotlinx.android.synthetic.main.vh_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class TeamDetailsFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_team_details

    private val viewModel by viewModelWithFragment<TeamDetailsViewModel>()

    private val navController: NavController by lazyNavController()

    override val fabConfiguration = FabConfiguration({ navController.navigate(R.id.nav_create_event, viewModel.teamData.toBundle()) }, R.drawable.ic_group_add)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            title.text = viewModel.teamData.name
            recyclerView.apply {
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.adapter = clickableListAdapter<EventData>(R.layout.vh_player, itemCallback()) { item, view ->
                    view.name.text = item.type
                    view.date.text = item.getDate().toStandardString()
                    view.subtitle.text = "${item.players.size} players"
                }.apply {
                    viewModel.events.observe(this@TeamDetailsFragment) { submitList(it) }
                    onItemClickListener { navController.navigate(R.id.nav_event_details, bundle(it, viewModel.teamData)) }
                }
//                recyclerView.adapter = simpleListAdapter<PlayerData>(R.layout.vh_player, itemCallback()) { item, view ->
//                    view.name.text = item.toString()
//                }.apply {
//                    viewModel.players.observe(this@TeamDetailsFragment) { submitList(it) }
//                }
            }
        }
    }
}

val teamDetailsModule = module {
    viewModel { (fragment: Fragment) -> TeamDetailsViewModel(get(), fragment.getParcelable()) }
}

class TeamDetailsViewModel(references: FirebaseReferences, val teamData: TeamData) : ViewModel() {

    val players = references.playersForTeamLiveDataForValueListener(teamData.key)

    val events = references.eventsForTeamLiveDataForValueListener(teamData.key)
}