package com.morcinek.players.ui.funino.create

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.SelectionListAdapter
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.*
import com.morcinek.players.core.extensions.mutableSetValueLiveData
import com.morcinek.players.core.extensions.observe
import com.morcinek.players.core.extensions.toBundle
import com.morcinek.players.core.itemCallback
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_which_players.view.*
import kotlinx.android.synthetic.main.vh_selectable_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class WhichPlayersFragment : BaseFragment(R.layout.fragment_which_players) {

    private val navController by lazyNavController()

    private val viewModel by viewModel<WhichPlayersViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = SelectionListAdapter<PlayerData>(R.layout.vh_selectable_player, itemCallback()) { _, item ->
                name.text = "$item"
            }.apply {
                selectedItems = viewModel.selectedPlayers.value!!
                observe(viewModel.players) { submitList(it) }
                onSelectedItemsChanged { viewModel.selectedPlayers.postValue(it) }
            }
        }
        viewModel.selectedPlayersNumber.observe(this@WhichPlayersFragment, Observer { view.selectedPlayersText.text = "$it" })
        view.nextButton.apply {
            viewModel.isNextEnabled.observe(this@WhichPlayersFragment, Observer { isEnabled = it })
            setOnClickListener { navController.navigate(R.id.nav_how_many_games, CreateTournamentData(viewModel.selectedPlayers.value!!).toBundle()) }
        }
    }
}

private class WhichPlayersViewModel(references: FirebaseReferences) : ViewModel() {

    val selectedPlayers = mutableSetValueLiveData<PlayerData>()
    val selectedPlayersNumber: LiveData<Int> = Transformations.map(selectedPlayers) { it.size }
    val isNextEnabled: LiveData<Boolean> = Transformations.map(selectedPlayers) { it.size in 6..20 }

    val players = references.playersLiveDataForValueListener()
}

val whichPlayersModule = module {
    viewModel { WhichPlayersViewModel(get()) }
}
