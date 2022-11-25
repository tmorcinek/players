package com.morcinek.players.ui.teams

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.morcinek.players.AppPreferences
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createFabConfiguration
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.eventsForTeamLiveDataForValueListener
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.database.playersWithoutTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.core.itemCallback
import com.morcinek.players.core.ui.showDeleteCodeConfirmationDialog
import com.morcinek.players.ui.lazyNavController
import com.morcinek.players.ui.teams.stats.PlayerStatsDetails
import com.morcinek.recyclerview.HasKey
import com.morcinek.recyclerview.listAdapter
import kotlinx.android.synthetic.main.fragment_team_details.view.*
import kotlinx.android.synthetic.main.vh_player.view.*
import kotlinx.android.synthetic.main.vh_player.view.subtitle
import kotlinx.android.synthetic.main.vh_stat.view.*
import kotlinx.android.synthetic.main.vh_stat.view.name
import kotlinx.android.synthetic.main.view_empty_players.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class TeamDetailsFragment : BaseFragment(R.layout.fragment_team_details) {

    private val viewModel by viewModelWithFragment<TeamDetailsViewModel>()

    private val navController by lazyNavController()
    private val appPreferences by inject<AppPreferences>()

    override val fabConfiguration =
        createFabConfiguration(R.drawable.ic_ball) { navController.navigate(R.id.nav_create_event, bundle { putString(viewModel.teamData.key) }) }

    override val menuConfiguration = createMenuConfiguration {
        addAction(R.string.add_players, R.drawable.ic_person_add) {
            observe(viewModel.playersWithoutTeam) {
                when {
                    it.isEmpty() -> navController.navigate(R.id.nav_create_player, viewModel.teamData.toBundle())
                    else -> navController.navigate(R.id.nav_add_players_to_team, viewModel.teamData.toBundle())
                }
            }
        }
        addAction(R.string.calendar_stats, R.drawable.ic_menu_tournament) {
            navController.navigatorProvider
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appPreferences.selectedTeamData = viewModel.teamData
        view.apply {
            tabLayout.setupWithViewPager(viewPager)
            observe(viewModel.hasPlayers) {
                tabLayout.isVisible = it
                viewPager.adapter = if (it) {
                    recyclerViewPagerAdapter(
                        R.string.page_general to generalAdapter(),
                        R.string.page_events to eventsAdapter(),
                        R.string.page_stats to statsAdapter(),
                        R.string.page_trainings to trainingsAdapter(),
                        R.string.page_stats_last_n to statsAdapterLast(20),
                        R.string.page_players to playersAdapter()
                    )
                } else {
                    singlePageAdapter(R.layout.view_empty_players) {
                        addPlayerButton.setOnClickListener { navController.navigate(R.id.nav_create_player, viewModel.teamData.toBundle()) }
                        deleteButton.setOnClickListener {
                            showDeleteCodeConfirmationDialog(
                                R.string.team_delete_query,
                                R.string.team_delete_message
                            ) { viewModel.deleteTeam { navController.popBackStack() } }
                        }
                    }.apply { notifyDataSetChanged() }
                }
            }
        }
        exitTransition = moveTransition()
    }

    private fun generalAdapter() = listAdapter<GeneralStats>(R.layout.vh_player, com.morcinek.recyclerview.itemCallback()) { _, item ->
        name.text = item.name
        date.text = "${item.value}"
    }.apply {
        liveData(viewLifecycleOwner, viewModel.general)
    }

    private val eventsFormatter = dayOfWeekDateFormat()
    private fun eventsAdapter() = listAdapter<EventData>(R.layout.vh_player, itemCallback()) { _, item ->
        name.text = item.type
        date.text = eventsFormatter.formatCalendar(item.getDate())
        subtitle.text = "${item.players.size} players"
        setOnClickListener { view ->
            navController.navigate(
                R.id.nav_event_details,
                bundle { putParcel(item); putString(viewModel.teamData.key) },
                null,
                FragmentNavigatorExtras(view.name, view.date)
            )
        }
    }.apply {
        liveData(viewLifecycleOwner, viewModel.events)
    }

    private fun statsAdapter() = listAdapter<PlayerStats>(R.layout.vh_stat, com.morcinek.recyclerview.itemCallback()) { position, item ->
        name.text = "${position + 1}. ${item.name}"
        attendance.text = item.attended.toString()
        points.text = item.points.toString()
        setOnClickListener { navController.navigate(R.id.nav_player_stats, bundle(PlayerStatsDetails(item.data, viewModel.playerEvents(item.data)))) }
    }.apply {
        liveData(viewLifecycleOwner, viewModel.playersStats)
    }

    private fun trainingsAdapter() = listAdapter<PlayerStats>(R.layout.vh_stat, com.morcinek.recyclerview.itemCallback()) { position, item ->
        name.text = "${position + 1}. ${item.name}"
        attendance.text = item.attended.toString()
        points.text = item.points.toString()
        setOnClickListener { navController.navigate(R.id.nav_player_stats, bundle(PlayerStatsDetails(item.data, viewModel.playerEvents(item.data)))) }
    }.apply {
        liveData(viewLifecycleOwner, viewModel.playersTrainingStats)
    }

    private fun statsAdapterLast(numberOfRecords: Int) =
        listAdapter<PlayerStats>(R.layout.vh_stat, com.morcinek.recyclerview.itemCallback()) { position, item ->
            name.text = "${position + 1}. ${item.name}"
            attendance.text = item.attended.toString()
            points.text = item.points.toString()
            setOnClickListener { navController.navigate(R.id.nav_player_stats, bundle(PlayerStatsDetails(item.data, viewModel.playerEvents(item.data)))) }
        }.apply {
            liveData(viewLifecycleOwner, viewModel.playersStatsLast(numberOfRecords))
        }

    private val playersFormatter = standardDateFormat()
    private fun playersAdapter() = listAdapter<PlayerData>(R.layout.vh_player, itemCallback()) { _, item ->
        name.text = item.toString()
        date.text = playersFormatter.formatCalendar(item.getBirthDate())
        setOnClickListener { navController.navigate(R.id.nav_player_details, bundle(item)) }
    }.apply {
        liveData(viewLifecycleOwner, viewModel.players)
    }
}

private class TeamDetailsViewModel(val references: FirebaseReferences, val teamData: TeamData) : ViewModel() {

    val players = references.playersForTeamLiveDataForValueListener(teamData.key)

    val events = references.eventsForTeamLiveDataForValueListener(teamData.key).map { it.sortedByDescending(EventData::dateInMillis) }
    val general = events.map {
        listOf(
            GeneralStats("Events", "${it.size}"),
            GeneralStats("Frequency", "${it.sumOf { it.players.size }.toDouble() / it.size}"),
            GeneralStats("Frequency games", "${it.filter { it.type == "Game" }.let { events -> events.sumOf { it.players.size }.toDouble() / events.size }}"),
            GeneralStats(
                "Frequency training",
                "${it.filter { it.type == "Training" }.let { events -> events.sumOf { it.players.size }.toDouble() / events.size }}"
            ),
            GeneralStats(
                "Frequency friendlies",
                "${it.filter { it.type == "Friendly" }.let { events -> events.sumOf { it.players.size }.toDouble() / events.size }}"
            ),
        ).plus(it.groupBy { it.type }.map { GeneralStats(it.key, "${it.value.size}") })
    }
//    "Training", "Game", "Tournament", "Friendly"

    fun playerEvents(player: PlayerData) = events.value!!.filter { !it.optional || player.key in it.players }

    val hasPlayers = players.map { it.isNotEmpty() }

    val playersStats = combine(players, events) { players, events ->
        players.map { player ->
            PlayerStats(
                name = player.toString(),
                attended = events.count { player.key in it.players },
                points = events.filter { player.key in it.players }.sumOf { it.playerPointsSum(player.key) },
                player
            )
        }.sortedByDescending { it.attended }
    }

    val playersTrainingStats = combine(players, events) { players, events ->
        players.map { player ->
            PlayerStats(
                name = player.toString(),
                attended = events.filter { it.type == "Training" }.count { player.key in it.players },
                points = events.filter { player.key in it.players }.sumOf { it.playerPointsSum(player.key) },
                player
            )
        }.sortedByDescending { it.attended }
    }

    fun playersStatsLast(numberOfRecords: Int) =
        combine(players, references.eventsForTeamLiveDataForValueListener(teamData.key, numberOfRecords)) { players, events ->
            players.map { player ->
                PlayerStats(
                    name = player.toString(),
                    attended = events.filter { it.type == "Training" }.count { player.key in it.players },
                    points = events.filter { player.key in it.players }.sumOf { it.playerPointsSum(player.key) },
                    player
                )
            }.sortedByDescending { it.attended }
        }

    val playersWithoutTeam = references.playersWithoutTeamLiveDataForValueListener()

    fun deleteTeam(doOnComplete: () -> Unit) = references.teamsReference().child(teamData.key).removeValue().addOnCompleteListener { doOnComplete() }
}

private class PlayerStats(
    val name: String,
    val attended: Int,
    val points: Int,
    val data: PlayerData,
    override val key: String = data.key
) : HasKey

private data class GeneralStats(val name: String, val value: String, override val key: String = name) : HasKey

val teamDetailsModule = module {
    viewModel { (fragment: Fragment) -> TeamDetailsViewModel(get(), fragment.getParcelable()) }
}
