package com.morcinek.players.ui.team

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import com.morcinek.android.itemCallback
import com.morcinek.core.lazyNavController
import com.morcinek.players.AppPreferences
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createFabConfiguration
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.eventTypeColor
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.eventsForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.databinding.FragmentPagerBinding
import com.morcinek.players.databinding.VhEventBinding
import com.morcinek.players.ui.teams.event.CreateEventFragment
import com.morcinek.players.ui.teams.event.EventDetailsFragment
import com.morcinek.players.ui.teams.stats.listAdapter
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class EventsFragment : BaseFragment<FragmentPagerBinding>(FragmentPagerBinding::inflate) {

    override val title = R.string.page_events

    private val viewModel by viewModel<EventsViewModel>()

    private val navController by lazyNavController()

    override val fabConfiguration =
        createFabConfiguration(R.drawable.ic_ball) { navController.navigate<CreateEventFragment>(bundle { putString(viewModel.teamData.key) }) }

    private val formatter = dayOfWeekDateFormat()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            tabLayout.setupWithViewPager(viewPager)
            viewPager.adapter = recyclerViewPagerAdapter(
                getString(R.string.all) to eventsAdapter().apply { liveData(viewLifecycleOwner, viewModel.events) },
                EventData.Type.Training.name to eventsAdapter().apply { liveData(viewLifecycleOwner, viewModel.trainings) },
                EventData.Type.Game.name to eventsAdapter().apply { liveData(viewLifecycleOwner, viewModel.games) },
                EventData.Type.Friendly.name to eventsAdapter().apply { liveData(viewLifecycleOwner, viewModel.friendlies) },
                EventData.Type.Tournament.name to eventsAdapter().apply { liveData(viewLifecycleOwner, viewModel.tournaments) },
            )
        }
    }

    private fun eventsAdapter() = listAdapter(itemCallback<EventData>(), VhEventBinding::inflate) { position, item ->
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
}

private class EventsViewModel(references: FirebaseReferences, appPreferences: AppPreferences) : ViewModel() {

    val teamData = appPreferences.selectedTeamData!!

    val events = references.eventsForTeamLiveDataForValueListener(teamData.key).map { it.sortedByDescending(EventData::dateInMillis) }

    val trainings = events.map { it.filter { it.type == EventData.Type.Training } }
    val games = events.map { it.filter { it.type == EventData.Type.Game } }
    val friendlies = events.map { it.filter { it.type == EventData.Type.Friendly } }
    val tournaments = events.map { it.filter { it.type == EventData.Type.Tournament } }
}

val eventsModule = module {
    viewModel { EventsViewModel(get(), get()) }
}