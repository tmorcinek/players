package com.morcinek.players.ui.funino.creator

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.ClickableListAdapter
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.toBundle
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.core.itemCallback
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_number.view.nextButton
import kotlinx.android.synthetic.main.fragment_number_games.view.*
import kotlinx.android.synthetic.main.vh_games_number.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class HowManyGamesFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_number_games

    private val navController: NavController by lazyNavController()

    private val viewModel by viewModelWithFragment<HowManyGamesViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 3)
            layoutAnimation = LayoutAnimationController(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in))
            adapter = HowManyGamesAdapter().apply {
                viewModel.gamesNumbers.observe(this@HowManyGamesFragment, Observer { submitList(it) })
                viewModel.selectedGamesNumber.observe(this@HowManyGamesFragment, Observer {
                    selectedItem = it
                    notifyDataSetChanged()
                })
                onClickListener { _, item -> viewModel.select(item) }
            }
        }
        viewModel.selectedGamesNumber.observe(this, Observer {
            view.nextButton.isEnabled = it != null
            it?.score?.let { view.message.text = "Games difference is ${it.first}\nDispersion ratio is ${it.second}" }
        })
        view.nextButton.setOnClickListener { navController.navigate(R.id.nav_what_colors, viewModel.createTournamentData.toBundle()) }
    }
}

private class HowManyGamesAdapter : ClickableListAdapter<GamesNumber>(itemCallback {
    areItemsTheSame { oldItem, newItem -> oldItem.numberOfGames == newItem.numberOfGames }
}) {

    var selectedItem: GamesNumber? = null

    override val vhResourceId = R.layout.vh_games_number

    override fun onBindViewHolder(item: GamesNumber, view: View) {
        super.onBindViewHolder(item, view)
        view.text.text = item.numberOfGames.toString()
        view.text.isSelected = item == selectedItem
    }
}

val howManyGamesModule = module {
    viewModel { (fragment: Fragment) -> HowManyGamesViewModel(fragment.getParcelable()) }
}

class HowManyGamesViewModel(val createTournamentData: CreateTournamentData) : ViewModel() {

    val selectedGamesNumber: LiveData<GamesNumber?> = MutableLiveData<GamesNumber?>().apply { value = null }
    fun select(gamesNumber: GamesNumber) = (selectedGamesNumber as MutableLiveData).postValue(gamesNumber)

    val gamesNumbers: LiveData<List<GamesNumber>> = MutableLiveData<List<GamesNumber>>().apply {
        value = listOf(
            GamesNumber(13, 0 to 14),
            GamesNumber(17, 1 to 36),
            GamesNumber(15, 0 to 50)
        )
    }
}

class GamesNumber(val numberOfGames: Int, val score: Pair<Int, Int>)