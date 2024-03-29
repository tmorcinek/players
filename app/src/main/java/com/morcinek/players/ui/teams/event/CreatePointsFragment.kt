package com.morcinek.players.ui.teams.event

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.morcinek.android.HasKey
import com.morcinek.android.itemCallback
import com.morcinek.android.list
import com.morcinek.core.lazyNavController
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.PointsData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.alert.alert
import com.morcinek.players.core.extensions.alert.okButton
import com.morcinek.players.core.extensions.combine
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.getString
import com.morcinek.players.core.extensions.map
import com.morcinek.players.core.extensions.observe
import com.morcinek.players.core.extensions.updateValue
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.databinding.FragmentCreatePointsBinding
import com.morcinek.players.databinding.VhPlayerPointsBinding
import com.morcinek.players.databinding.ViewNumberPickerBinding
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class CreatePointsFragment : BaseFragment<FragmentCreatePointsBinding>(FragmentCreatePointsBinding::inflate) {

    override val title = R.string.menu_create_points

    private val viewModel by viewModelWithFragment<CreatePointsViewModel>()

    private val navController by lazyNavController()

    override val menuConfiguration = createMenuConfiguration {
        addAction(R.string.action_done, R.drawable.ic_done) {
            observe(viewModel.isNextEnabled) {
                if (it) {
                    viewModel.submitPoints().addOnCompleteListener { navController.popBackStack() }
                } else {
                    alert("No points") { okButton { } }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            recyclerView.list(itemCallback<PlayerPoints>(), VhPlayerPointsBinding::inflate) {
                onBind { _, player ->
                    name.text = player.name
                    name.setTextColor(resources.getColor(player.nameColor))
                    fun ViewNumberPickerBinding.bindPointsLayout(player: PlayerPoints, minValue: Int = 0, maxValue: Int = 10, step: Int = 1) {
                        pointsLabel.text = "${player.points}"
                        plusButton.run {
                            isEnabled = player.points < maxValue
                            setOnClickListener { viewModel.updatePoints(player.key, player.points + step) }
                        }
                        minusButton.run {
                            isEnabled = player.points > minValue
                            setOnClickListener { viewModel.updatePoints(player.key, player.points - step) }
                        }
                    }
                    if (viewModel.eventData.type == EventData.Type.Training) {
                        pointsLayout.bindPointsLayout(player, -1, 1)
                    } else {
                        pointsLayout.bindPointsLayout(player)
                    }
                }
                liveData(viewLifecycleOwner, viewModel.playersPoints)
            }
        }
    }
}

private class CreatePointsViewModel(private val references: FirebaseReferences, val teamKey: String, val eventData: EventData) : ViewModel() {

    private val pointsId: Int? = eventData.points.firstOrNull()?.id

    private val players = references.playersForTeamLiveDataForValueListener(teamKey).map { it.filter { it.key in eventData.players } }

    private val points = MutableLiveData(eventData.points.find { it.id == pointsId }?.playersPoints ?: mapOf())

    private fun pointsData() = PointsData("", pointsId ?: eventData.points.size, points.value!!)

    fun updatePoints(playerKey: String, playerPoints: Int) {
        points.updateValue { plus(playerKey to playerPoints) }
    }

    fun submitPoints(): Task<Void> = references.teamEventsReference(teamKey).child(eventData.key).child("points").setValue(
        if (pointsId == null) eventData.points.plus(pointsData()) else eventData.points.filterNot { it.id == pointsId }.plus(pointsData())
    )

    val playersPoints = combine(players, points) { players, points ->
        players.map {
            (points[it.key] ?: 0).let { playerPoints ->
                PlayerPoints(it.toString(), if (playerPoints != 0) R.color.textPrimary else R.color.textDisabled, playerPoints, it.key)
            }
        }
    }

    val isNextEnabled = playersPoints.map { it.any { it.points != 0 } }
}

private data class PlayerPoints(val name: String, val nameColor: Int, val points: Int, override val key: String) : HasKey

val createPointsModule = module {
    viewModel { (fragment: Fragment) -> CreatePointsViewModel(get(), fragment.getString(), fragment.getParcelable()) }
}
