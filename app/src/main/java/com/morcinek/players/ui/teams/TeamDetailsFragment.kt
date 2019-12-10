package com.morcinek.players.ui.teams

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
import com.morcinek.players.ui.teams.stats.PlayerStatsDetails
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

    override val menuConfiguration = createMenuConfiguration(R.menu.edit) {
        addAction(R.id.add_players) { navController.navigate(R.id.nav_add_players_to_team, viewModel.teamData.toBundle()) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            title.text = viewModel.teamData.name
            tabLayout.setupWithViewPager(viewPager)
            viewPager.adapter = recyclerViewPagerAdapter(
                R.string.page_events to eventsAdapter(),
                R.string.page_stats to statsAdapter(),
                R.string.page_players to playersAdapter()
            )
        }
    }


    private val eventsFormatter = dayOfWeekDateFormat()
    private fun eventsAdapter() = clickableListAdapter<EventData>(R.layout.vh_player, itemCallback()) { _, item, view ->
        view.name.text = item.type
        view.date.text = eventsFormatter.formatCalendar(item.getDate())
        view.subtitle.text = "${item.players.size} players"
    }.apply {
        observe(viewModel.events) { submitList(it) }
        onItemClickListener { navController.navigate(R.id.nav_event_details, bundle(it, viewModel.teamData)) }
    }

    private fun statsAdapter() = clickableListAdapter<PlayerStats>(R.layout.vh_stat, itemCallback()) { position, item, view ->
        view.name.text = "${position + 1}. ${item.name}"
        view.attendance.text = item.attended.toString()
        view.missed.text = item.missed.toString()
    }.apply {
        observe(viewModel.playersStats) { submitList(it) }
        onItemClickListener { navController.navigate(R.id.nav_player_stats, bundle(PlayerStatsDetails(it.data, viewModel.playerEvents(it.data)))) }
    }

    private val playersFormatter = standardDateFormat()
    private fun playersAdapter() = clickableListAdapter<PlayerData>(R.layout.vh_player, itemCallback()) { _, item, view ->
        view.name.text = item.toString()
        view.date.text = playersFormatter.formatCalendar(item.getBirthDate())
    }.apply {
        observe(viewModel.players) { submitList(it) }
        onItemClickListener { navController.navigate(R.id.nav_player_details, bundle(it)) }
    }
}

val teamDetailsModule = module {
    viewModel { (fragment: Fragment) -> TeamDetailsViewModel(get(), fragment.getParcelable()) }
}

private class TeamDetailsViewModel(references: FirebaseReferences, val teamData: TeamData) : ViewModel() {

    val players = references.playersForTeamLiveDataForValueListener(teamData.key)

    val events = references.eventsForTeamLiveDataForValueListener(teamData.key).map { it.sortedByDescending { it.dateInMillis } }

    fun playerEvents(player: PlayerData) = events.value!!.filter { !it.optional || player.key in it.players }

    val playersStats = combine(players, events) { player, events ->
        player.map { player ->
            PlayerStats(
                player.toString(), events.count { player.key in it.players }, events.count { !it.optional && player.key !in it.players }, player
            )
        }.sortedByDescending { it.attended }
    }
}

private class PlayerStats(val name: String, val attended: Int, val missed: Int, val data: PlayerData) : HasKey by data
