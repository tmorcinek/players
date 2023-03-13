package com.morcinek.players.ui.teams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.morcinek.android.HasKey
import com.morcinek.android.itemCallback
import com.morcinek.players.AppPreferences
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createFabConfiguration
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.eventsForTeamLiveDataForValueListener
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.database.playersWithoutTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.core.ui.showDeleteCodeConfirmationDialog
import com.morcinek.players.databinding.FragmentTeamDetailsBinding
import com.morcinek.players.databinding.VhStatBinding
import com.morcinek.players.databinding.ViewEmptyPlayersBinding
import com.morcinek.players.ui.lazyNavController
import com.morcinek.players.ui.teams.stats.PlayerStatsDetails
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class TeamDetailsFragment : BaseFragment<FragmentTeamDetailsBinding>(FragmentTeamDetailsBinding::inflate) {

    private val viewModel by viewModel<TeamDetailsViewModel>()

    private val navController by lazyNavController()
    private val appPreferences by inject<AppPreferences>()

    override val fabConfiguration = createFabConfiguration(R.drawable.ic_ball) { navController.navigate(R.id.nav_create_event, bundle { putString(viewModel.teamData.key) }) }

    override val menuConfiguration = createMenuConfiguration {
        addAction(R.string.add_players, R.drawable.ic_person_add) {
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
        appPreferences.selectedTeamData = viewModel.teamData
        binding.run {
            tabLayout.setupWithViewPager(viewPager)
            observe(viewModel.hasPlayers) {
                tabLayout.isVisible = it
                viewPager.adapter = if (it) {
                    recyclerViewPagerAdapter(
                        R.string.all to statsAdapter(),
                        R.string.page_trainings to trainingsAdapter(),
                        R.string.page_stats_last_20 to statsAdapterLast(20),
                    )
                } else {
                    singlePageAdapter(ViewEmptyPlayersBinding::inflate) {
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

    override fun onResume() {
        navController.currentDestination?.label = viewModel.teamData.name
        super.onResume()
    }

    private fun <T, B : ViewBinding> listAdapter(
        diffCallback: DiffUtil.ItemCallback<T>,
        createBinding: (LayoutInflater, ViewGroup?, Boolean) -> B,
        onBindView: B.(position: Int, item: T) -> Unit
    ) = com.morcinek.android.listAdapter(diffCallback, createBinding) { onBind(onBindView) }

    private fun statsAdapter() = listAdapter(itemCallback<PlayerStats>(), VhStatBinding::inflate) { position, item ->
        name.text = "${position + 1}. ${item.name}"
        attendance.text = item.attended.toString()
        points.text = item.points.toString()
        root.setOnClickListener { navController.navigate(R.id.nav_player_stats, bundle(PlayerStatsDetails(item.data, viewModel.playerEvents(item.data)))) }
    }.apply {
        liveData(viewLifecycleOwner, viewModel.playersStats)
    }

    private fun trainingsAdapter() = listAdapter(itemCallback<PlayerStats>(), VhStatBinding::inflate) { position, item ->
        name.text = "${position + 1}. ${item.name}"
        attendance.text = item.attended.toString()
        points.text = item.points.toString()
        root.setOnClickListener { navController.navigate(R.id.nav_player_stats, bundle(PlayerStatsDetails(item.data, viewModel.playerEvents(item.data)))) }
    }.apply { liveData(viewLifecycleOwner, viewModel.playersTrainingStats) }

    private fun statsAdapterLast(numberOfRecords: Int) =
        listAdapter(itemCallback<PlayerStats>(), VhStatBinding::inflate) { position, item ->
            name.text = "${position + 1}. ${item.name}"
            attendance.text = item.attended.toString()
            points.text = item.points.toString()
            root.setOnClickListener { navController.navigate(R.id.nav_player_stats, bundle(PlayerStatsDetails(item.data, viewModel.playerEvents(item.data)))) }
        }.apply { liveData(viewLifecycleOwner, viewModel.playersStatsLast(numberOfRecords)) }

}

private class TeamDetailsViewModel(val references: FirebaseReferences, val appPreferences: AppPreferences) : ViewModel() {

    val teamData = appPreferences.selectedTeamData!!

    val players = references.playersForTeamLiveDataForValueListener(teamData.key)

    val events = references.eventsForTeamLiveDataForValueListener(teamData.key).map { it.sortedByDescending(EventData::dateInMillis) }

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
                    attended = events.count { player.key in it.players },
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

val teamDetailsModule = module {
    viewModel { TeamDetailsViewModel(get(), get()) }
}
