package com.morcinek.players.ui.teams.event


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.morcinek.android.HasKey
import com.morcinek.android.setupSections
import com.morcinek.core.lazyNavController
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.eventTypeColor
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.eventForTeamLiveDataForValueListener
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.bundle
import com.morcinek.players.core.extensions.combineWith
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.getString
import com.morcinek.players.core.extensions.map
import com.morcinek.players.core.extensions.moveTransition
import com.morcinek.players.core.extensions.observe
import com.morcinek.players.core.extensions.observeNonNull
import com.morcinek.players.core.extensions.putInt
import com.morcinek.players.core.extensions.putParcel
import com.morcinek.players.core.extensions.putString
import com.morcinek.players.core.extensions.toDayOfWeekDateFormat
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.core.ui.showDeleteCodeConfirmationDialog
import com.morcinek.players.databinding.FragmentEventDetailsBinding
import com.morcinek.players.databinding.VhPlayerEventHeaderBinding
import com.morcinek.players.databinding.VhPlayerEventPointsBinding
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class EventDetailsFragment : BaseFragment<FragmentEventDetailsBinding>(FragmentEventDetailsBinding::inflate) {

    override val title = R.string.menu_event_details

    private val viewModel by viewModelWithFragment<EventDetailsViewModel>()

    private val navController by lazyNavController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = moveTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            observeNonNull(viewModel.event) {
                title.run {
                    text = it.type?.name
                    background.setTint(eventTypeColor(it.type))
                }
                year.text = it.getDate().toDayOfWeekDateFormat()
                status.text = getString(R.string.players_count, it.players.size)
            }
            recyclerView.setupSections {
                sectionBinding(VhPlayerEventPointsBinding::inflate) { _, item: PlayerWithPoints ->
                    name.text = item.name
                    pointsSum.text = item.sum?.toString()
                }
                sectionBinding(VhPlayerEventHeaderBinding::inflate) { _, _: Header -> }
                observe(viewModel.items) { submitList(it) }
            }
        }
    }

    override val menuConfiguration = createMenuConfiguration {
        addAction(R.string.action_delete, R.drawable.ic_delete) {
            showDeleteCodeConfirmationDialog(
                R.string.delete_event_query,
                R.string.delete_event_message
            ) { viewModel.deleteEvent { navController.popBackStack() } }
        }
        addAction(R.string.action_edit, R.drawable.ic_edit) {
            navController.navigate<CreateEventFragment>(bundle {
                putInt(R.string.menu_edit_event)
                putString(viewModel.teamKey)
                putParcel(viewModel.event.value!!)
            })
        }
        addAction(R.string.event_action_points, R.drawable.ic_star_half) {
            navController.navigate<CreatePointsFragment>(bundle {
                putString(viewModel.teamKey)
                putParcel(viewModel.event.value!!)
            })
        }
    }
}

private class EventDetailsViewModel(val references: FirebaseReferences, val teamKey: String, eventData: EventData) : ViewModel() {

    val event = references.eventForTeamLiveDataForValueListener(teamKey, eventData.key)

    private val playersWithPoints = references.playersForTeamLiveDataForValueListener(teamKey).combineWith(event) { players, event ->
        if (event != null) {
            players.filter { it.key in event.players }.map { player ->
                event.points.map { it.playersPoints[player.key] ?: 0 }.let { points -> PlayerWithPoints(player.toString(), points.sum().takeIf { it != 0 }, player.key) }
            }
        } else {
            listOf()
        }
    }

    val items = playersWithPoints.map { listOf<HasKey>(Header()).plus(it) }

    fun deleteEvent(doOnComplete: () -> Unit) = references.teamEventReference(teamKey, event.value!!.key).removeValue().addOnCompleteListener { doOnComplete() }
}

private data class PlayerWithPoints(
    val name: String,
    val sum: Int?,
    override val key: String,
) : HasKey

private data class Header(override val key: String = "Header") : HasKey

val eventDetailsModule = module {
    viewModel { (fragment: Fragment) -> EventDetailsViewModel(get(), fragment.getString(), fragment.getParcelable()) }
}
