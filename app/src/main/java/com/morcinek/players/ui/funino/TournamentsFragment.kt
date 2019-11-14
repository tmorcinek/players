package com.morcinek.players.ui.funino

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.ClickableListAdapter
import com.morcinek.players.core.FabConfiguration
import com.morcinek.players.core.extensions.toBundle
import com.morcinek.players.core.itemCallback
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.vh_tournament.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class TournamentsFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_list

    private val viewModel by viewModel<TournamentsViewModel>()

    private val navController: NavController by lazyNavController()

    override val fabConfiguration = FabConfiguration({ navController.navigate(R.id.nav_how_many_players) })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            layoutAnimation = LayoutAnimationController(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in))
            adapter = TournamentAdapter().apply {
                viewModel.players.observe(this@TournamentsFragment, Observer { submitList(it) })
                onClickListener { _, item ->
                    navController.navigate(R.id.nav_tournament_details, item.toBundle())
                }
            }
        }
    }
}

class TournamentAdapter : ClickableListAdapter<TournamentData>(itemCallback {
    areItemsTheSame { oldItem, newItem -> oldItem.id == newItem.id }
}) {

    override val vhResourceId = R.layout.vh_tournament

    override fun onBindViewHolder(item: TournamentData, view: View) {
        view.apply {
            title.text = item.title
            subtitle.text = item.subtitle
            finished.text = item.finished
            isToday.isVisible = item.isToday
        }
    }
}

val funinoModule = module {
    viewModel { TournamentsViewModel() }
}