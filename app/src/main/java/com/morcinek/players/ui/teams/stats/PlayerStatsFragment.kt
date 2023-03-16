package com.morcinek.players.ui.teams.stats

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.morcinek.android.HasKey
import com.morcinek.android.itemCallback
import com.morcinek.android.listAdapter
import com.morcinek.android.setup
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.extensions.*
import com.morcinek.players.databinding.FragmentPlayerStatsBinding
import com.morcinek.players.databinding.VhPlayerEventCircleBinding
import com.morcinek.core.lazyNavController
import com.morcinek.players.ui.players.PlayerDetailsFragment
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.text.NumberFormat


class PlayerStatsFragment : BaseFragment<FragmentPlayerStatsBinding>(FragmentPlayerStatsBinding::inflate) {

    private val viewModel by viewModelWithFragment<PlayerStatsViewModel>()

    private val navController by lazyNavController()

    override val menuConfiguration = createMenuConfiguration {
        addAction(R.string.menu_player_details, R.drawable.ic_profile) { navController.navigate<PlayerDetailsFragment>(viewModel.playerStatsDetails.playerData.toBundle()) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            title.text = viewModel.title()
            subtitle.text = viewModel.subtitle()
            recyclerView.setup {
                adapter(listAdapter(itemCallback<PlayerEvent>(), VhPlayerEventCircleBinding::inflate) {
                    onBind { _, item ->
                        circleText.text = item.date
                        circleText.setBackgroundResource(item.statusColor)
                    }
                    submitList(viewModel.events())
                })
                grid(6)
            }
        }
    }
}

private class PlayerStatsViewModel(val playerStatsDetails: PlayerStatsDetails) : ViewModel() {

    private val dateFormat = circleDayDate()

    fun title() = "${playerStatsDetails.playerData}"

    fun subtitle() = "Frequency: $playerStatsDetails"

    fun events() = playerStatsDetails.events.map { PlayerEvent(it.type, dateFormat.formatCalendar(it.getDate()), eventBackground(it), it.key, it) }

    private fun eventBackground(it: EventData) = when (playerStatsDetails.playerData.key in it.players) {
        true -> R.drawable.circle_green
        false -> R.drawable.circle_red
    }
}

@Parcelize
class PlayerStatsDetails(val playerData: PlayerData, val events: List<EventData>) : Parcelable {
//    @IgnoredOnParcel val trainings = events.filter { it.type == "Training" }
    @IgnoredOnParcel val attendence = events.count { playerData.key in it.players }
    @IgnoredOnParcel val ratio = attendence.toDouble() / events.size

    override fun toString() = "     ${attendence}/${events.size}      ${NumberFormat.getPercentInstance().format(ratio)}"
}

private data class PlayerEvent(val name: String, val date: String, val statusColor: Int, override var key: String, val eventData: EventData) : HasKey

val playerStatsModule = module {
    viewModel { (fragment: Fragment) -> PlayerStatsViewModel(fragment.getParcelable()) }
}
