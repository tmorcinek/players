package com.morcinek.players.ui.players

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import com.morcinek.android.list
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.android.HasKey
import com.morcinek.android.itemCallback
import com.morcinek.players.core.createFabConfiguration
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.playersLiveDataForValueListener
import com.morcinek.players.core.database.teamsLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.databinding.FragmentListBinding
import com.morcinek.players.databinding.VhPlayerBinding
import com.morcinek.players.ui.lazyNavController
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class PlayersFragment : BaseFragment<FragmentListBinding>(FragmentListBinding::inflate) {

    private val viewModel by viewModel<PlayersViewModel>()

    private val navController by lazyNavController()

    override val fabConfiguration = createFabConfiguration(R.drawable.ic_person_add) { navController.navigate(R.id.nav_create_player) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            progressBar.show()
            recyclerView.list(itemCallback<PlayerItem>(), VhPlayerBinding::inflate) {
                onBind { _, item ->
                    name.text = item.name
                    subtitle.text = item.subtitle
                    date.text = item.date
                    root.setOnClickListener {
                        navController.navigate(
                            R.id.nav_player_details,
                            item.data.toBundle(),
                            null,
                            FragmentNavigatorExtras(name, subtitle, date)
                        )
                    }
                }
                liveData(viewLifecycleOwner, viewModel.players) { progressBar.hide() }
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