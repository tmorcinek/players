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
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.eventsForTeamLiveDataForValueListener
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.database.playersWithoutTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.ui.lazyNavController
import com.morcinek.players.ui.teams.stats.PlayerStatsDetails
import kotlinx.android.synthetic.main.fragment_team_details.view.*
import kotlinx.android.synthetic.main.vh_player.view.*
import kotlinx.android.synthetic.main.vh_stat.view.*
import kotlinx.android.synthetic.main.vh_stat.view.name
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class TeamDetailsFragment : BaseFragment(R.layout.fragment_team_details) {

    private val viewModel by viewModelWithFragment<TeamDetailsViewModel>()

    private val navController: NavController by lazyNavController()

    override val fabConfiguration = FabConfiguration({ navController.navigate(R.id.nav_create_event, viewModel.teamData.toBundle()) }, R.drawable.ic_group_add)

    override val menuConfiguration = createMenuConfiguration {
        addAction(R.string.add_players, R.drawable.ic_add) {
            observe(viewModel.playersWithoutTeam) {
                when {
                    it.isEmpty() -> navController.navigate(R.id.nav_create_player, viewModel.teamData.toBundle())
                    else -> navController.navigate(R.id.nav_add_players_to_team, viewModel.teamData.toBundle())
                }
            }
        }
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
        exitTransition = moveTransition()
    }


    private val eventsFormatter = dayOfWeekDateFormat()
    private fun eventsAdapter() = listAdapter<EventData>(R.layout.vh_player, itemCallback()) { _, item ->
        name.text = item.type
        date.text = eventsFormatter.formatCalendar(item.getDate())
        subtitle.text = "${item.players.size} players"
        setOnClickListener { view ->
            navController.navigate(R.id.nav_event_details, bundle(item, viewModel.teamData), null, FragmentNavigatorExtras(view.name, view.date))
        }
    }.apply {
        observe(viewModel.events) { submitList(it) }
    }

    private fun statsAdapter() = listAdapter<PlayerStats>(R.layout.vh_stat, itemCallback()) { position, item ->
        name.text = "${position + 1}. ${item.name}"
        attendance.text = item.attended.toString()
        missed.text = item.missed.toString()
        setOnClickListener { navController.navigate(R.id.nav_player_stats, bundle(PlayerStatsDetails(item.data, viewModel.playerEvents(item.data)))) }
    }.apply {
        observe(viewModel.playersStats) { submitList(it) }
    }

    private val playersFormatter = standardDateFormat()
    private fun playersAdapter() = listAdapter<PlayerData>(R.layout.vh_player, itemCallback()) { _, item ->
        name.text = item.toString()
        date.text = playersFormatter.formatCalendar(item.getBirthDate())
        setOnClickListener { navController.navigate(R.id.nav_player_details, bundle(item)) }
    }.apply {
        observe(viewModel.players) { submitList(it) }
    }
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

    val playersWithoutTeam = references.playersWithoutTeamLiveDataForValueListener()
}

private class PlayerStats(val name: String, val attended: Int, val missed: Int, val data: PlayerData) : HasKey by data

val teamDetailsModule = module {
    viewModel { (fragment: Fragment) -> TeamDetailsViewModel(get(), fragment.getParcelable()) }
}
