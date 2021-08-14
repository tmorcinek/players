package com.morcinek.players.ui.players

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.*
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.playersLiveDataForValueListener
import com.morcinek.players.core.database.teamsLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.ui.lazyNavController
import com.morcinek.recyclerview.list
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.vh_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class PlayersFragment : BaseFragment(R.layout.fragment_list) {

    private val viewModel by viewModel<PlayersViewModel>()

    private val navController by lazyNavController()

    override val fabConfiguration = createFabConfiguration(R.drawable.ic_person_add) { navController.navigate(R.id.nav_create_player) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            progressBar.show()
            recyclerView.list<PlayerItem>(itemCallback()){
                resId(R.layout.vh_player)
                onBind { _, item ->
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
                }
                liveData(viewLifecycleOwner, viewModel.players) { view.progressBar.hide() }
            }
        }
        exitTransition = moveTransition()
    }
}

private class PlayersViewModel(references: FirebaseReferences) : ViewModel() {

    private val dateFormat = standardDateFormat()

    val players = combine(references.playersLiveDataForValueListener(), references.teamsLiveDataForValueListener()) { player, team ->
        player
            .map { PlayerItem(it.toString(), team.find { team -> team.key == it.teamKey }?.name ?: "", dateFormat.formatCalendar(it.getBirthDate()), it) }
            .sortedBy { it.data.teamKey }
    }
}

private class PlayerItem(val name: String, val subtitle: String, val date: String, val data: PlayerData) : HasKey by data

val playersModule = module {
    viewModel { PlayersViewModel(get()) }
}