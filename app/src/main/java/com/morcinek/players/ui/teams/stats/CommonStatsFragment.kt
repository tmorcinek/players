package com.morcinek.players.ui.teams.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.morcinek.android.HasKey
import com.morcinek.android.itemCallback
import com.morcinek.core.NavController
import com.morcinek.core.lazyNavController
import com.morcinek.players.AppPreferences
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createFabConfiguration
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.eventsForTeamLiveDataForValueListener
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.database.playersWithoutTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.bundle
import com.morcinek.players.core.extensions.combine
import com.morcinek.players.core.extensions.map
import com.morcinek.players.core.extensions.moveTransition
import com.morcinek.players.core.extensions.observe
import com.morcinek.players.core.extensions.putString
import com.morcinek.players.core.extensions.recyclerViewPagerAdapter
import com.morcinek.players.core.extensions.singlePageAdapter
import com.morcinek.players.core.extensions.toBundle
import com.morcinek.players.core.ui.showDeleteCodeConfirmationDialog
import com.morcinek.players.databinding.FragmentPagerBinding
import com.morcinek.players.databinding.VhStatBinding
import com.morcinek.players.databinding.ViewEmptyPlayersBinding
import com.morcinek.players.ui.players.CreatePlayerFragment
import com.morcinek.players.ui.teams.event.CreateEventFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class CommonStatsFragment : BaseFragment<FragmentPagerBinding>(FragmentPagerBinding::inflate) {

    override val title = R.string.page_common_stats

    private val viewModel by viewModel<TeamDetailsViewModel>()

    private val navController by lazyNavController()
    private val appPreferences by inject<AppPreferences>()

    override val fabConfiguration = createFabConfiguration(R.drawable.ic_ball) { navController.navigate<CreateEventFragment>(bundle { putString(viewModel.teamData.key) }) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appPreferences.selectedTeamData = viewModel.teamData
        binding.run {
            tabLayout.setupWithViewPager(viewPager)
            observe(viewModel.hasPlayers) {
                tabLayout.isVisible = it
                viewPager.adapter = if (it) {
                    recyclerViewPagerAdapter(
                        getString(R.string.all) to playerStatsAdapter(viewModel.playersStats),
                        getString(R.string.page_trainings) to playerStatsAdapter(viewModel.playersTrainingStats),
                        getString(R.string.page_stats_last_20) to playerStatsAdapter(viewModel.playersStatsLast(20)),
                    )
                } else {
                    singlePageAdapter(ViewEmptyPlayersBinding::inflate) {
                        addPlayerButton.setOnClickListener { navController.navigate<CreatePlayerFragment>(viewModel.teamData.toBundle()) }
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

    private fun playerStatsAdapter(liveData: LiveData<List<PlayerStats>>) = listAdapter(itemCallback<PlayerStats>(), VhStatBinding::inflate) { position, item ->
        name.text = "${position + 1}. ${item.name}"
        attendance.text = item.attended.toString()
        points.text = item.points.toString()
        root.setOnClickListener { navController.navigateToPlayerStatsFragment(item, viewModel.playerEvents(item.data)) }
    }.apply { liveData(viewLifecycleOwner, liveData) }


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
                attended = events.filter { it.type == EventData.Type.Training }.count { player.key in it.players },
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

internal class PlayerStats(
    val name: String,
    val attended: Int,
    val points: Int,
    val data: PlayerData,
    override val key: String = data.key,
) : HasKey

val teamDetailsModule = module {
    viewModel { TeamDetailsViewModel(get(), get()) }
}

fun <T, B : ViewBinding> listAdapter(
    diffCallback: DiffUtil.ItemCallback<T>,
    createBinding: (LayoutInflater, ViewGroup?, Boolean) -> B,
    onBindView: B.(position: Int, item: T) -> Unit,
) = com.morcinek.android.listAdapter(diffCallback, createBinding) { onBind(onBindView) }

internal fun NavController.navigateToPlayerStatsFragment(item: PlayerStats, events: List<EventData>) = navigate<PlayerStatsFragment>(bundle(PlayerStatsDetails(item.data, events)))
