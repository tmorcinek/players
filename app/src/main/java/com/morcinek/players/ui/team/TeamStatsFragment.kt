package com.morcinek.players.ui.team

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import com.morcinek.android.HasKey
import com.morcinek.android.itemCallback
import com.morcinek.android.list
import com.morcinek.players.AppPreferences
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.eventsForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.map
import com.morcinek.players.core.extensions.toStandardString
import com.morcinek.players.databinding.FragmentListBinding
import com.morcinek.players.databinding.VhPlayerBinding
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class TeamStatsFragment : BaseFragment<FragmentListBinding>(FragmentListBinding::inflate) {

    override val title = R.string.team_stats

    private val viewModel by viewModel<TeamStatsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            progressBar.show()
            recyclerView.list(itemCallback<GeneralStats>(), VhPlayerBinding::inflate) {
                onBind { _, item ->
                    name.text = item.name
                    date.text = item.value
                }
                liveData(viewLifecycleOwner, viewModel.general) { progressBar.hide() }
            }
        }
    }
}

private class TeamStatsViewModel(references: FirebaseReferences, appPreferences: AppPreferences) : ViewModel() {

    val teamData = appPreferences.selectedTeamData!!

    val general = references.eventsForTeamLiveDataForValueListener(teamData.key).map {
        listOf(
            GeneralStats("First Training", it.sortedBy(EventData::dateInMillis).first().getDate().toStandardString()),
            GeneralStats("Number of players", "${it.fold(setOf<String>()) { acc, eventData -> acc.plus(eventData.players)  }.count()}"),
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
}

private data class GeneralStats(val name: String, val value: String, override val key: String = name) : HasKey

val teamStatsModule = module {
    viewModel { TeamStatsViewModel(get(), get()) }
}