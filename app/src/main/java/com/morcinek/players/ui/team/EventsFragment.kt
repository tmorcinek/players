package com.morcinek.players.ui.team

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import com.morcinek.android.itemCallback
import com.morcinek.android.list
import com.morcinek.players.AppPreferences
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createFabConfiguration
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.eventsForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.databinding.FragmentListBinding
import com.morcinek.players.databinding.VhEventBinding
import com.morcinek.core.lazyNavController
import com.morcinek.players.ui.teams.event.CreateEventFragment
import com.morcinek.players.ui.teams.event.EventDetailsFragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class EventsFragment : BaseFragment<FragmentListBinding>(FragmentListBinding::inflate) {

    private val viewModel by viewModel<EventsViewModel>()

    private val navController by lazyNavController()

    override val fabConfiguration =
        createFabConfiguration(R.drawable.ic_ball) { navController.navigate<CreateEventFragment>(bundle { putString(viewModel.teamData.key) }) }

    private val formatter = dayOfWeekDateFormat()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            progressBar.show()
            recyclerView.list(itemCallback<EventData>(), VhEventBinding::inflate) {
                onBind { _, item ->
                    name.text = item.type
                    date.text = formatter.formatCalendar(item.getDate())
                    subtitle.text = "${item.players.size} players"
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

private class EventsViewModel(references: FirebaseReferences, appPreferences: AppPreferences) : ViewModel() {

    val teamData = appPreferences.selectedTeamData!!

    val events = references.eventsForTeamLiveDataForValueListener(teamData.key).map { it.sortedByDescending(EventData::dateInMillis) }
}

val eventsModule = module {
    viewModel { EventsViewModel(get(), get()) }
}