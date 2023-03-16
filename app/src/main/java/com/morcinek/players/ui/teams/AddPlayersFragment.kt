package com.morcinek.players.ui.teams

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.core.lazyNavController
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.playersWithoutTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.databinding.FragmentAddPlayersBinding
import com.morcinek.players.ui.players.CreatePlayerFragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class AddPlayersFragment : BaseFragment<FragmentAddPlayersBinding>(FragmentAddPlayersBinding::inflate) {

    private val viewModel by viewModelWithFragment<AddPlayersViewModel>()

    private val navController by lazyNavController()

    override val menuConfiguration = createMenuConfiguration {
        addAction(R.string.add_players, R.drawable.ic_person_add) { navController.navigate<CreatePlayerFragment>() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            title.text = viewModel.teamData.name
            recyclerView.run {
                layoutManager = LinearLayoutManager(activity)
//                adapter = SelectionListAdapter<PlayerData>(R.layout.vh_selectable_player, itemCallback()) { _, item ->
//                    name.text = "$item"
//                }.apply {
//                    selectedItems = viewModel.selectedPlayers.value!!
//                    observe(viewModel.players) { submitList(it) }
//                    onSelectedItemsChanged { viewModel.selectedPlayers.postValue(it) }
//                }
            }
            nextButton.run {
                observe(viewModel.isNextEnabled) { isEnabled = it }
                setOnClickListener { viewModel.addPlayersToTeam { } }
            }
        }
    }
}

private class AddPlayersViewModel(val references: FirebaseReferences, val teamData: TeamData) : ViewModel() {

    val selectedPlayers = mutableSetValueLiveData<PlayerData>()

    fun addPlayersToTeam(doOnComplete: () -> Unit) {
        val childUpdates = selectedPlayers.value!!.map { "${it.key}/teamKey" to teamData.key }.toMap()
        references.playersReference().updateChildren(childUpdates).addOnCompleteListener { doOnComplete() }
    }

    val players = references.playersWithoutTeamLiveDataForValueListener()

    val isNextEnabled: LiveData<Boolean> = selectedPlayers.map { it.isNotEmpty() }
}

val addPlayersModule = module {
    viewModel { (fragment: Fragment) -> AddPlayersViewModel(get(), fragment.getParcelable()) }
}
