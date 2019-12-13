package com.morcinek.players.ui.players

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.*
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.*
import com.morcinek.players.core.extensions.*
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.vh_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class PlayersFragment : BaseFragment(R.layout.fragment_list) {

    private val viewModel by viewModel<PlayersViewModel>()

    private val navController: NavController by lazyNavController()

    override val fabConfiguration = FabConfiguration({ navController.navigate(R.id.nav_create_player) })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            progressBar.show()
            recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = listAdapter(R.layout.vh_player, itemCallback()) { _, item: PlayerItem ->
                    name.text = item.name
                    subtitle.text = item.subtitle
                    date.text = item.date
                    setOnClickListener {
                        navController.navigate(
                            R.id.nav_player_details,
                            item.data.toBundle(),
                            null,
                            FragmentNavigatorExtras(view.name, view.subtitle, view.date)
                        )
                    }
                }.apply {
                    observe(viewModel.players) {
                        submitList(it)
                        view.progressBar.hide()
                    }
                }
            }
        }
        exitTransition = moveTransition()
    }
}

private class PlayersViewModel(references: FirebaseReferences) : ViewModel() {

    private val dateFormat = standardDateFormat()

    val players =
        combine(references.playersLiveDataForValueListener(), references.teamsLiveDataForValueListener()) { player, team ->
            player.map { PlayerItem(it.toString(), team.find { team -> team.key == it.teamKey }?.name ?: "", dateFormat.formatCalendar(it.getBirthDate()), it) }
                .sortedBy { it.data.teamKey }
        }
}

private class PlayerItem(val name: String, val subtitle: String, val date: String, val data: PlayerData) : HasKey by data

val playersModule = module {
    viewModel { PlayersViewModel(get()) }
}