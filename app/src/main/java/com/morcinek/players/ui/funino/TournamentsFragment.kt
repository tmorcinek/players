package com.morcinek.players.ui.funino

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.FabConfiguration
import com.morcinek.players.core.database.observe
import com.morcinek.players.core.extensions.toBundle
import com.morcinek.players.core.itemCallback
import com.morcinek.players.core.listAdapter
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.vh_tournament.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class TournamentsFragment : BaseFragment(R.layout.fragment_list) {

    private val viewModel by viewModel<TournamentsViewModel>()

    private val navController: NavController by lazyNavController()

    override val fabConfiguration = FabConfiguration({ navController.navigate(R.id.nav_how_many_players) })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = listAdapter(R.layout.vh_tournament, itemCallback { areItemsTheSame { t1, t2 -> t1.id == t2.id } }) { _, item: TournamentData ->
                title.text = item.title
                subtitle.text = item.subtitle
                finished.text = item.finished
                isToday.isVisible = item.isToday
                setOnClickListener { navController.navigate(R.id.nav_tournament_details, item.toBundle()) }
            }.apply {
                observe(viewModel.tournaments) { submitList(it) }
            }
        }
    }
}

val funinoModule = module {
    viewModel { TournamentsViewModel() }
}

private class TournamentsViewModel : ViewModel() {

    val tournaments: LiveData<List<TournamentData>> = MutableLiveData<List<TournamentData>>()
}

