package com.morcinek.players.ui.players.details

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.MenuConfiguration
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.map
import com.morcinek.players.core.database.observe
import com.morcinek.players.core.database.teamsLiveDataForValueListener
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.toStandardString
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class PlayerDetailsFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_player
    override val menuConfiguration = MenuConfiguration(R.menu.player_details,
        R.id.delete to { Toast.makeText(requireContext(), "Delete", Toast.LENGTH_SHORT).show() },
        R.id.edit to { Toast.makeText(requireContext(), "Edit", Toast.LENGTH_SHORT).show() }
    )

    private val viewModel by viewModelWithFragment<PlayerDetailsViewModel>()

    private val navController: NavController by lazyNavController()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            view.name.text = playerData.toString()
            view.birthDate.text = playerData.getBirthDate().toStandardString()
            playerTeam.observe(this@PlayerDetailsFragment) {
                view.team.text = it?.name ?: "-"
            }
//            navController.
        }
    }
}

val playerDetailsModule = module {
    viewModel { (fragment: Fragment) -> PlayerDetailsViewModel(get(), fragment.getParcelable()) }
}

class PlayerDetailsViewModel(references: FirebaseReferences, val playerData: PlayerData) : ViewModel() {

    private val teams = references.teamsLiveDataForValueListener()

    val playerTeam = teams.map { it.find { it.key == playerData.teamKey } }
}
