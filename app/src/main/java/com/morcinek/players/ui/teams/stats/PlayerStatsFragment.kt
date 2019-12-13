package com.morcinek.players.ui.teams.stats

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.*
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.extensions.dayOfWeekDateFormat
import com.morcinek.players.core.extensions.formatCalendar
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.viewModelWithFragment
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_player_stats.view.*
import kotlinx.android.synthetic.main.vh_player_event.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class PlayerStatsFragment : BaseFragment(R.layout.fragment_player_stats) {

    private val viewModel by viewModelWithFragment<PlayerStatsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            title.text = viewModel.title()
            recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = listAdapter<PlayerEvent>(R.layout.vh_player_event, itemCallback()) { _, item ->
                    name.text = item.name
                    date.text = item.date
                    setBackgroundResource(item.statusColor)
                }.apply {
                    submitList(viewModel.events())
                }
            }
        }
    }
}

private class PlayerStatsViewModel(val playerStatsDetails: PlayerStatsDetails) : ViewModel() {

    private val dateFormat = dayOfWeekDateFormat()

    fun title() =
        "${playerStatsDetails.playerData} (${playerStatsDetails.events.count { playerStatsDetails.playerData.key in it.players }}/${playerStatsDetails.events.size})"

    fun events() = playerStatsDetails.events.map { PlayerEvent(it.type, dateFormat.formatCalendar(it.getDate()), eventStatus(it), it.key) }

    private fun eventStatus(it: EventData) = when{
       it.optional -> R.color.dark_indigo_30
        playerStatsDetails.playerData.key in it.players -> R.color.colorPrimary30
        else -> R.color.colorAccent30
    }
}

@Parcelize
class PlayerStatsDetails(val playerData: PlayerData, val events: List<EventData>) : Parcelable

private data class PlayerEvent(val name: String, val date: String, val statusColor: Int, override var key: String) : HasKey

val playerStatsModule = module {
    viewModel { (fragment: Fragment) -> PlayerStatsViewModel(fragment.getParcelable()) }
}
