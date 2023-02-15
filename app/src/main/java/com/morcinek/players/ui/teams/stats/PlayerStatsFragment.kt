package com.morcinek.players.ui.teams.stats

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.HasKey
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.extensions.*
import com.morcinek.players.core.itemCallback
import com.morcinek.players.databinding.FragmentPlayerStatsBinding
import com.morcinek.players.ui.lazyNavController
import com.morcinek.recyclerview.list
import com.morcinek.recyclerview.setup
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_player_stats.view.*
import kotlinx.android.synthetic.main.vh_games_number.view.*
import kotlinx.android.synthetic.main.vh_player_event_circle.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.text.NumberFormat


class PlayerStatsFragment : BaseFragment<FragmentPlayerStatsBinding>(FragmentPlayerStatsBinding::inflate) {

    private val viewModel by viewModelWithFragment<PlayerStatsViewModel>()

    private val navController by lazyNavController()

    override val menuConfiguration = createMenuConfiguration {
        addAction(R.string.menu_player_details, R.drawable.ic_profile) {
            navController.navigate(R.id.nav_player_details, viewModel.playerStatsDetails.playerData.toBundle())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            title.text = viewModel.title()
            subtitle.text = viewModel.subtitle()
            recyclerView.setup {
                list<PlayerEvent>(itemCallback()){
                    resId(R.layout.vh_player_event_circle)
                    onBind { _, item ->
                        circleText.text = item.date
                        circleText.setBackgroundResource(item.statusColor)
                    }
                    submitList(viewModel.events())
                }
                grid(6)
            }
//            recyclerView.list<PlayerEvent>(itemCallback()) {
//                resId(R.layout.vh_player_event)
//                onBind { _, item ->
//                    name.text = item.name
//                    date.text = item.date
//                    setBackgroundResource(item.statusColor)
//                    setOnClickListener {
//                        navController.navigate(
//                            R.id.nav_event_details,
//                            bundle {
//                                putParcel(item.eventData)
//                                putString(viewModel.playerStatsDetails.playerData.teamKey!!)
//                            }
//                        )
//                    }
//                }
//                submitList(viewModel.events())
//            }
        }
    }
}

private class PlayerStatsViewModel(val playerStatsDetails: PlayerStatsDetails) : ViewModel() {

    private val dateFormat = circleDayDate()

    fun title() = "${playerStatsDetails.playerData}"

    fun subtitle() = "Frequency: $playerStatsDetails"

    fun events() = playerStatsDetails.trainings.map { PlayerEvent(it.type, dateFormat.formatCalendar(it.getDate()), eventBackground(it), it.key, it) }

    private fun eventStatus(it: EventData) = when {
        it.optional -> R.color.dark_indigo_30
        playerStatsDetails.playerData.key in it.players -> R.color.colorPrimary30
        else -> R.color.colorAccent30
    }

    private fun eventBackground(it: EventData) = when(playerStatsDetails.playerData.key in it.players) {
        true -> R.drawable.circle_green
        false -> R.drawable.circle_red
    }
}

@Parcelize
class PlayerStatsDetails(val playerData: PlayerData, val events: List<EventData>) : Parcelable {
    val trainings = events.filter { it.type == "Training" }
    val attendence = trainings.count { playerData.key in it.players }
    val ratio = attendence.toDouble() / trainings.size

    override fun toString() = "     ${attendence}/${trainings.size}      ${NumberFormat.getPercentInstance().format(ratio)}"
}

private data class PlayerEvent(val name: String, val date: String, val statusColor: Int, override var key: String, val eventData: EventData) : HasKey

val playerStatsModule = module {
    viewModel { (fragment: Fragment) -> PlayerStatsViewModel(fragment.getParcelable()) }
}
