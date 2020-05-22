package com.morcinek.players.ui.funino.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.SelectionListAdapter
import com.morcinek.players.core.SingleSelect
import com.morcinek.players.core.extensions.observe
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.toBundle
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.core.itemCallback
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_number_games.view.*
import kotlinx.android.synthetic.main.vh_games_number.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class HowManyGamesFragment : BaseFragment(R.layout.fragment_number_games) {

    private val navController by lazyNavController()

    private val viewModel by viewModelWithFragment<HowManyGamesViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter =
                SelectionListAdapter<GamesNumber>(
                    R.layout.vh_games_number, itemCallback { areItemsTheSame { i1, i2 -> i1.numberOfGames == i2.numberOfGames } }, SingleSelect
                ) { _, item ->
                    text.text = item.numberOfGames.toString()
                }.apply {
                    selectedItems = viewModel.selectedGamesNumber.value?.let { setOf(it) } ?: setOf()
                    observe(viewModel.gamesNumbers) { submitList(it) }
                    onSelectedItemsChanged { viewModel.select(it.firstOrNull()) }
                }
        }
        viewModel.selectedGamesNumber.observe(this, Observer {
            view.nextButton.isEnabled = it != null
            it?.score?.let { view.message.text = "Games difference is ${it.first}\nDispersion ratio is ${it.second}" }
        })
        view.nextButton.setOnClickListener { navController.navigate(R.id.nav_what_colors, viewModel.createTournamentData.toBundle()) }
    }
}

val howManyGamesModule = module {
    viewModel { (fragment: Fragment) -> HowManyGamesViewModel(fragment.getParcelable()) }
}

private const val NUMBER_OF_GAMES_TO_CHOOSE_FROM = 15

class HowManyGamesViewModel(val createTournamentData: CreateTournamentData) : ViewModel() {

    private val teamsGenerator = TeamsGenerator()
    private val allGames = gamesCombination(createTournamentData.numberOfPlayers)

    val selectedGamesNumber = MutableLiveData<GamesNumber?>()

    fun select(gamesNumber: GamesNumber?) {
        selectedGamesNumber.postValue(gamesNumber)
        createTournamentData.games = gamesNumber?.let { allGames.take(it.numberOfGames) } ?: listOf()
    }

    val gamesNumbers: LiveData<List<GamesNumber>> = MutableLiveData<List<GamesNumber>>().apply {
        value = combinations().map { GamesNumber(it.second.size, it.first) }
    }

    private fun combinations() = allGames.let { games ->
        (6..games.size).map {
            games.take(it).let { gamesSubset -> teamsGenerator.overallScore(createTournamentData.numberOfPlayers, gamesSubset) to gamesSubset }
        }.sortedBy { it.first.first * 10 + it.first.second }.take(NUMBER_OF_GAMES_TO_CHOOSE_FROM)
    }
}

class GamesNumber(val numberOfGames: Int, val score: Pair<Int, Int>)