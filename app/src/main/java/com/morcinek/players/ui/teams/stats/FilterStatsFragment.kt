package com.morcinek.players.ui.teams.stats

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morcinek.android.itemCallback
import com.morcinek.android.list
import com.morcinek.core.lazyNavController
import com.morcinek.core.ui.show
import com.morcinek.players.AppPreferences
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.eventsForTeamLiveDataForValueListener
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.combine
import com.morcinek.players.core.extensions.map
import com.morcinek.players.databinding.FragmentListBinding
import com.morcinek.players.databinding.VhStatBinding
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class FilterStatsFragment : BaseFragment<FragmentListBinding>(FragmentListBinding::inflate) {

    override val title = R.string.filter

    private val viewModel by viewModel<FilterStatsViewModel>()

    private val navController by lazyNavController()

    override val menuConfiguration = createMenuConfiguration {
        addAction(R.string.filter, R.drawable.ic_filter) { FilterBottomSheetDialogFragment().apply { filterData = viewModel.filterData }.show(requireActivity().supportFragmentManager) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            progressBar.show()
            recyclerView.list(itemCallback<PlayerStats>(), VhStatBinding::inflate) {
                onBind { position, item ->
                    name.text = "${position + 1}. ${item.name}"
                    attendance.text = item.attended.toString()
                    points.text = item.points.toString()
                    root.setOnClickListener { navController.navigateToPlayerStatsFragment(item, viewModel.playerEvents(item.data)) }
                }
                liveData(viewLifecycleOwner, viewModel.playersStats) { progressBar.hide() }
            }
        }
    }
}

private class FilterStatsViewModel(val references: FirebaseReferences, val appPreferences: AppPreferences) : ViewModel() {

    private val teamData = appPreferences.selectedTeamData!!

    val filterData = MutableLiveData(FilterData())

    private val players = references.playersForTeamLiveDataForValueListener(teamData.key)

    private val events = references.eventsForTeamLiveDataForValueListener(teamData.key).map { it.sortedByDescending(EventData::dateInMillis) }

    private val filteredEvents = combine(events, filterData) { eventData, filterData -> eventData.filter { it.type in filterData.types || filterData.types.isEmpty() }.let { filterData.period.events(it) }}

    val playersStats = combine(players, filteredEvents) { players, events ->
        players.map { player ->
            PlayerStats(
                name = player.toString(),
                attended = events.count { player.key in it.players },
                points = events.filter { player.key in it.players }.sumOf { it.playerPointsSum(player.key) },
                player
            )
        }.sortedByDescending { it.attended }
    }

    fun playerEvents(player: PlayerData) = events.value!!.filter { !it.optional || player.key in it.players }
}

val filterStatsModule = module {
    viewModel { FilterStatsViewModel(get(), get()) }
}

