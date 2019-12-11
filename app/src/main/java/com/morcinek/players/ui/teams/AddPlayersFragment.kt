package com.morcinek.players.ui.teams

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.observe
import com.morcinek.players.core.database.playersWithoutTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.core.itemCallback
import com.morcinek.players.core.selectableListAdapter
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_add_players.view.*
import kotlinx.android.synthetic.main.vh_selectable_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class AddPlayersFragment : BaseFragment(R.layout.fragment_add_players) {

    private val viewModel by viewModelWithFragment<AddPlayersViewModel>()

    private val navController: NavController by lazyNavController()

    override val menuConfiguration = createMenuConfiguration(R.menu.add) {
        addAction(R.id.add_players) { navController.navigate(R.id.nav_create_player) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.title.text = viewModel.teamData.name
        view.nextButton.apply {
            viewModel.isNextEnabled.observe(this@AddPlayersFragment) { isEnabled = it }
            setOnClickListener {
                viewModel.addPlayersToTeam { viewModel.clearSelectedPlayer() }
            }
        }
        view.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = selectableListAdapter<PlayerData>(R.layout.vh_selectable_player, itemCallback()) { _, item, view ->
                view.name.text = "$item"
            }.apply {
                viewModel.players.observe(this@AddPlayersFragment) { submitList(it) }
                viewModel.selectedPlayers.observe(this@AddPlayersFragment) { selectedItems = it }
                onClickListener { _, item -> viewModel.select(item) }
            }
        }
    }
}

val addPlayersModule = module {
    viewModel { (fragment: Fragment) -> AddPlayersViewModel(get(), fragment.getParcelable()) }
}

private class AddPlayersViewModel(val references: FirebaseReferences, val teamData: TeamData) : ViewModel() {


    fun addPlayersToTeam(doOnComplete: () -> Unit) {
        val childUpdates = selectedPlayers.value!!.map { "${it.key}/teamKey" to teamData.key }.toMap()
        references.playersReference().updateChildren(childUpdates).addOnCompleteListener { doOnComplete() }
    }

    val players = references.playersWithoutTeamLiveDataForValueListener()

    val selectedPlayers: LiveData<Set<PlayerData>> = MutableLiveData<Set<PlayerData>>().apply { value = setOf() }

    val isNextEnabled: LiveData<Boolean> = Transformations.map(selectedPlayers) { it.isNotEmpty() }

    fun select(player: PlayerData) = (selectedPlayers as MutableLiveData).apply {
        postValue(updateSelectedItem(player))
    }

    fun clearSelectedPlayer() = (selectedPlayers as MutableLiveData).postValue(setOf())
}

fun <T> LiveData<Set<T>>.updateSelectedItem(item: T) = value!!.let { selectedItems ->
    if (item in selectedItems) selectedItems.minus(item) else selectedItems.plus(item)
}