package com.morcinek.players.ui.players

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.teamsLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.core.ui.showDeleteCodeConfirmationDialog
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class PlayerDetailsFragment : BaseFragment(R.layout.fragment_player) {

    private val viewModel by viewModelWithFragment<PlayerDetailsViewModel>()

    private val navController by lazyNavController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = moveTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            viewModel.playerData.let {
                name.text = it.toString()
                birthDate.text = it.getBirthDate().toStandardString()
            }
            observe(viewModel.playerTeam) {
                team.text = it?.name ?: "-"
            }
        }
    }

    override val menuConfiguration by lazy {
        createMenuConfiguration {
            if (getParcelable<PlayerData>().teamKey == null) {
                addAction(R.string.action_delete, R.drawable.ic_delete) {
                    showDeleteCodeConfirmationDialog(
                        R.string.player_delete_query,
                        R.string.player_delete_message
                    ) { viewModel.deletePlayer { navController.popBackStack() } }
                }
            }
            addAction(R.string.action_edit, R.drawable.ic_edit) { navController.navigate(R.id.nav_edit_player, bundle(viewModel.playerData)) }
        }
    }
}

class PlayerDetailsViewModel(private val references: FirebaseReferences, val playerData: PlayerData) : ViewModel() {

    val playerTeam = references.teamsLiveDataForValueListener().map { it.find { it.key == playerData.teamKey } }

    fun deletePlayer(doOnComplete: () -> Unit) = references.playersReference().child(playerData.key).removeValue().addOnCompleteListener { doOnComplete() }
}

val playerDetailsModule = module {
    viewModel { (fragment: Fragment) -> PlayerDetailsViewModel(get(), fragment.getParcelable()) }
}
