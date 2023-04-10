package com.morcinek.players.ui.teams.stats

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import com.morcinek.android.itemCallback
import com.morcinek.android.list
import com.morcinek.core.lazyNavController
import com.morcinek.core.ui.showFragmentDialog
import com.morcinek.players.AppPreferences
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.eventTypeColor
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.eventsForTeamLiveDataForValueListener
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.databinding.FragmentListBinding
import com.morcinek.players.databinding.VhEventBinding
import com.morcinek.players.ui.teams.event.EventDetailsFragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class FilterStatsFragment : BaseFragment<FragmentListBinding>(FragmentListBinding::inflate) {

    override val title = R.string.page_events

    private val viewModel by viewModel<FilterStatsViewModel>()

    private val navController by lazyNavController()

    override val menuConfiguration = createMenuConfiguration {
        addAction(R.string.filter, R.drawable.ic_filter) { showFragmentDialog<FilterBottomSheetDialogFragment>() }
    }

    private val formatter = dayOfWeekDateFormat()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            progressBar.show()
            recyclerView.list(itemCallback<EventData>(), VhEventBinding::inflate) {
                onBind { _, item ->
                    name.run {
                        text = item.type?.name
                        background.setTint(eventTypeColor(item.type!!))
                    }
                    date.text = formatter.formatCalendar(item.getDate())
                    subtitle.text = getString(R.string.players_count, item.players.size)
                    root.setOnClickListener {
                        navController.navigate<EventDetailsFragment>(
                            bundle { putParcel(item); putString(viewModel.teamData.key) },
                            null,
                            FragmentNavigatorExtras(name, date)
                        )
                    }
                }
                liveData(viewLifecycleOwner, viewModel.events) { progressBar.hide() }
            }
        }
    }
}

private class FilterStatsViewModel(val references: FirebaseReferences, val appPreferences: AppPreferences) : ViewModel() {

    val teamData = appPreferences.selectedTeamData!!

    val players = references.playersForTeamLiveDataForValueListener(teamData.key)

    val events = references.eventsForTeamLiveDataForValueListener(teamData.key).map { it.sortedByDescending(EventData::dateInMillis) }
}

val filterStatsModule = module {
    viewModel { FilterStatsViewModel(get(), get()) }
}

