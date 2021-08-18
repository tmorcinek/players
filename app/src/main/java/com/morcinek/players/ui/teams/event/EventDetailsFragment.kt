package com.morcinek.players.ui.teams.event


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.PointsData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.eventForTeamLiveDataForValueListener
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.core.ui.showDeleteCodeConfirmationDialog
import com.morcinek.players.ui.lazyNavController
import com.morcinek.recyclerview.HasKey
import com.morcinek.recyclerview.sections
import kotlinx.android.synthetic.main.fragment_event_details.*
import kotlinx.android.synthetic.main.vh_player_event_points.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class EventDetailsFragment : BaseFragment(R.layout.fragment_event_details) {

    private val viewModel by viewModelWithFragment<EventDetailsViewModel>()

    private val navController by lazyNavController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = moveTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            observe(viewModel.event) {
                title.text = it.type
                year.text = it.getDate().toDayOfWeekDateFormat()
                status.apply {
                    setText(it.statusText())
                    setTextColor(resources.getColor(it.statusColor()))
                }
            }
            recyclerView.sections {
                section<PlayerWithPoints>(R.layout.vh_player_event_points) { _, item ->
                    name.text = item.name
                    pointsLayout.removeAllViews()
                    item.points.forEach {
                        pointsLayout.addView((LayoutInflater.from(context).inflate(R.layout.vh_point, pointsLayout, false) as TextView).apply { text = "$it" })
                    }
                    pointsSum.text = "${item.sum}"
                }
                section<Header>(R.layout.vh_player_event_points) {_, item ->
                    pointsLayout.removeAllViews()
                    item.pointsDataList.forEach { pointsData ->
                        pointsLayout.addView((LayoutInflater.from(context).inflate(R.layout.view_points_button, pointsLayout, false)).apply {
                            setOnClickListener {
                                navController.navigate(R.id.nav_create_points, bundle {
                                    putString(viewModel.teamKey)
                                    putParcel(viewModel.event.value!!)
                                    putInt(pointsData.id)
                                })
                            }
                        })
                    }
                }
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
            navController.navigate(R.id.nav_edit_event, bundle {
                putString(viewModel.teamKey)
                putParcel(viewModel.event.value!!)
            })
        }
        addAction(R.string.event_action_points, R.drawable.ic_menu_tournament) {
            navController.navigate(R.id.nav_create_points, bundle {
                putString(viewModel.teamKey)
                putParcel(viewModel.event.value!!)
            })
        }
    }
}

private class EventDetailsViewModel(val references: FirebaseReferences, val teamKey: String, eventData: EventData) : ViewModel() {

    val event = references.eventForTeamLiveDataForValueListener(teamKey, eventData.key)

    private val playersWithPoints = references.playersForTeamLiveDataForValueListener(teamKey).combineWith(event) { players, event ->
        players.filter { it.key in event.players }.map { player ->
            event.points.map { it.playersPoints[player.key] ?: 0 }.let { points -> PlayerWithPoints(player.toString(), points, points.sum(), player.key) }
        }
    }

    val items = playersWithPoints.map { listOf<HasKey>(Header(event.value!!.points)).plus(it) }

    fun deleteEvent(doOnComplete: () -> Unit) =
        references.teamEventReference(teamKey, event.value!!.key).removeValue().addOnCompleteListener { doOnComplete() }
}

private data class PlayerWithPoints(
    val name: String,
    val points: List<Int>,
    val sum: Int,
    override val key: String
) : HasKey

private data class Header(
    val pointsDataList: List<PointsData>,
    override val key: String = "Header"
): HasKey

private fun EventData.statusText() = if (optional) R.string.optional else R.string.mandatory
private fun EventData.statusColor() = if (optional) R.color.colorPrimary else R.color.colorAccent

val eventDetailsModule = module {
    viewModel { (fragment: Fragment) -> EventDetailsViewModel(get(), fragment.getString(), fragment.getParcelable()) }
}
