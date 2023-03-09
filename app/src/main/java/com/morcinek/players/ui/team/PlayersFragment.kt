package com.morcinek.players.ui.team

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import com.morcinek.android.itemCallback
import com.morcinek.android.list
import com.morcinek.players.AppPreferences
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createFabConfiguration
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
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

    override val fabConfiguration = createFabConfiguration(R.drawable.ic_person_add) { navController.navigate(R.id.action_nav_players_to_nav_create_player, viewModel.teamData.toBundle()) }

    private val playersFormatter = standardDateFormat()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            progressBar.show()
            recyclerView.list(itemCallback<PlayerData>(), VhPlayerBinding::inflate) {
                onBind { position, item ->
                    name.text = "${position + 1}. $item"
                    date.text = playersFormatter.formatCalendar(item.getBirthDate())
                    root.setOnClickListener {
                        navController.navigate(
                            R.id.action_nav_players_to_nav_player_details,
                            item.toBundle(),
                            null,
                            FragmentNavigatorExtras(name, date)
                        )
                    }
                }
                liveData(viewLifecycleOwner, viewModel.players) { progressBar.hide() }
            }
        }
    }
}

private class PlayersViewModel(references: FirebaseReferences, appPreferences: AppPreferences) : ViewModel() {

    val teamData = appPreferences.selectedTeamData!!

    val players = references.playersForTeamLiveDataForValueListener(teamData.key)
}

val playersModule = module {
    viewModel { PlayersViewModel(get(), get()) }
}