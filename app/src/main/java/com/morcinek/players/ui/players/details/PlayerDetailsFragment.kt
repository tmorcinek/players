package com.morcinek.players.ui.players.details

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.map
import com.morcinek.players.core.database.observe
import com.morcinek.players.core.database.teamsLiveDataForValueListener
import com.morcinek.players.core.extensions.alert.alert
import com.morcinek.players.core.extensions.alert.noButton
import com.morcinek.players.core.extensions.alert.yesButton
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.toStandardString
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class PlayerDetailsFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_player

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
        }
    }

    override val menuConfiguration = createMenuConfiguration(R.menu.player_details) {
        actions(
            R.id.delete to {
                alert(R.string.player_delete_message) {
                    yesButton { viewModel.deletePlayer { navController.popBackStack() } }
                    noButton {}
                }.show()
            },
            R.id.edit to { Toast.makeText(requireContext(), "Edit", Toast.LENGTH_SHORT).show() }
        )
        visibilities(R.id.delete to { viewModel.playerData.teamKey == null })
    }
}

val playerDetailsModule = module {
    viewModel { (fragment: Fragment) -> PlayerDetailsViewModel(get(), fragment.getParcelable()) }
}

class PlayerDetailsViewModel(val references: FirebaseReferences, val playerData: PlayerData) : ViewModel() {

    private val teams = references.teamsLiveDataForValueListener()

    val playerTeam = teams.map { it.find { it.key == playerData.teamKey } }

    fun deletePlayer(doOnComplete: () -> Unit) = references.playersReference().child(playerData.key).removeValue().addOnCompleteListener { doOnComplete() }
}
