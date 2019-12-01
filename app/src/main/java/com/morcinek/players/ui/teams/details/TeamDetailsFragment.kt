package com.morcinek.players.ui.teams.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.morcinek.players.R
import com.morcinek.players.core.*
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.*
import com.morcinek.players.core.extensions.*
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_team_details.view.*
import kotlinx.android.synthetic.main.vh_player.view.*
import kotlinx.android.synthetic.main.vh_stat.view.*
import kotlinx.android.synthetic.main.vh_stat.view.name
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
            tabLayout.setupWithViewPager(viewPager)
            viewPager.adapter = recyclerViewPagerAdapter(
                "Events" to clickableListAdapter<EventData>(R.layout.vh_player, itemCallback()) { item, view ->
                    view.name.text = item.type
                    view.date.text = item.getDate().toStandardString()
                    view.subtitle.text = "${item.players.size} players"
                }.apply {
                    observe(viewModel.events) { submitList(it) }
                    onItemClickListener { navController.navigate(R.id.nav_event_details, bundle(it, viewModel.teamData)) }
                },
                "Stats" to clickableListAdapter<PlayerStat>(R.layout.vh_stat, itemCallback()) { item, view ->
                    view.name.text = item.name
                    view.attendance.text = item.attended.toString()
                    view.missed.text = item.missed.toString()
                }.apply {
                    observe(viewModel.playersStats) { submitList(it) }
                },
                "Players" to  simpleListAdapter<PlayerData>(R.layout.vh_player, itemCallback()) { item, view ->
                    view.name.text = item.toString()
                }.apply {
                    observe(viewModel.players) { submitList(it) }
                }
            )
        }
    }
}

val teamDetailsModule = module {
    viewModel { (fragment: Fragment) -> TeamDetailsViewModel(get(), fragment.getParcelable()) }
}

private class TeamDetailsViewModel(references: FirebaseReferences, val teamData: TeamData) : ViewModel() {

    val players = references.playersForTeamLiveDataForValueListener(teamData.key)

    val events = references.eventsForTeamLiveDataForValueListener(teamData.key).map { it.sortedByDescending { it.dateInMillis } }

    val playersStats = combine(players, events) { player, events ->
        player.map { player -> PlayerStat(player.name, events.count { player.key in it.players }, events.count { player.key !in it.players }, player) }
            .sortedByDescending { it.attended }
    }
}

private class PlayerStat(val name: String, val attended: Int, val missed: Int, val data: PlayerData) : HasKey by data
