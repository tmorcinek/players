package com.morcinek.players.ui.teams.stats

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.HasKey
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.extensions.dayOfWeekDateFormat
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.core.itemCallback
import com.morcinek.players.core.simpleListAdapter
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_player_stats.view.*
import kotlinx.android.synthetic.main.vh_player_event.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class PlayerStatsFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_player_stats

    private val viewModel by viewModelWithFragment<PlayerStatsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            title.text = viewModel.title()
            recyclerView.apply {
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.adapter = simpleListAdapter<PlayerEvent>(R.layout.vh_player_event, itemCallback()) { _, item, view ->
                    view.name.text = item.name
                    view.date.text = item.date
                    view.setBackgroundResource(if (item.present) R.color.colorPrimary30 else R.color.colorAccent30)
                }.apply {
                    submitList(viewModel.events())
                }
            }
        }
    }
}

val playerStatsModule = module {
    viewModel { (fragment: Fragment) -> PlayerStatsViewModel(fragment.getParcelable()) }
}

private class PlayerStatsViewModel(val playerStatsView: PlayerStatsView) : ViewModel() {

    private val dateFormat = dayOfWeekDateFormat()

    fun title() =
        "${playerStatsView.playerData} (${playerStatsView.events.count { playerStatsView.playerData.key in it.players }}/${playerStatsView.events.size})"

    fun events() = playerStatsView.events.map { PlayerEvent(it.type, dateFormat.format(it.getDate().time), playerStatsView.playerData.key in it.players, it.key) }
}

@Parcelize
class PlayerStatsView(val playerData: PlayerData, val events: List<EventData>) : Parcelable

private class PlayerEvent(val name: String, val date: String, val present: Boolean, override var key: String) : HasKey