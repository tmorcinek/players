package com.morcinek.players.ui.funino.creator

import android.os.Bundle
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.SelectableListAdapter
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.playersLiveDataForValueListener
import com.morcinek.players.core.extensions.toBundle
import com.morcinek.players.core.itemCallback
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_which_players.view.*
import kotlinx.android.synthetic.main.vh_selectable_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class WhichPlayersFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_which_players

    private val navController: NavController by lazyNavController()

    private val viewModel by viewModel<WhichPlayersViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = WhichPlayersAdapter().apply {
                viewModel.players.observe(this@WhichPlayersFragment, Observer { submitList(it) })
                viewModel.selectedPlayers.observe(this@WhichPlayersFragment, Observer { selectedItems = it })
                onClickListener { _, item -> viewModel.select(item) }
            }
        }
        viewModel.selectedPlayersNumber.observe(this@WhichPlayersFragment, Observer { view.selectedPlayersText.text = "$it" })
        view.nextButton.apply {
            viewModel.isNextEnabled.observe(this@WhichPlayersFragment, Observer { isEnabled = it })
            setOnClickListener { navController.navigate(R.id.nav_how_many_games, CreateTournamentData(viewModel.selectedPlayers.value!!).toBundle()) }
        }
    }
}

class WhichPlayersAdapter : SelectableListAdapter<PlayerData>(R.layout.vh_selectable_player, itemCallback()) {

    override fun onBindViewHolder(item: PlayerData, view: View) {
        super.onBindViewHolder(item, view)
        view.name.text = "$item"
    }
}

val whichPlayersModule = module {
    viewModel { WhichPlayersViewModel(get()) }
}

private class WhichPlayersViewModel(references: FirebaseReferences) : ViewModel() {

    val selectedPlayers: LiveData<Set<PlayerData>> = MutableLiveData<Set<PlayerData>>().apply { value = setOf() }
    val selectedPlayersNumber: LiveData<Int> = Transformations.map(selectedPlayers) { it.size }
    val isNextEnabled: LiveData<Boolean> = Transformations.map(selectedPlayers) { it.size in 6..20 }

    fun select(player: PlayerData) = (selectedPlayers as MutableLiveData).postValue(updateSelectedPlayer(player))

    private fun updateSelectedPlayer(color: PlayerData) = selectedPlayers.value!!.let { selectedColors ->
        if (color in selectedColors) selectedColors.minus(color) else selectedColors.plus(color)
    }

    val players: LiveData<List<PlayerData>> = references.playersLiveDataForValueListener()
}
