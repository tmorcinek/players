package com.morcinek.players.ui.teams.details

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.SimpleListAdapter2
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.itemCallback
import kotlinx.android.synthetic.main.fragment_team_details.view.*
import kotlinx.android.synthetic.main.vh_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class TeamDetailsFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_team_details

    private val viewModel by viewModel<TeamDetailsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            viewModel.team.observe(this@TeamDetailsFragment, Observer {
                title.text = it.name
                category.text = it.category
                year.text = it.year.toString()
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.layoutAnimation = LayoutAnimationController(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in))
                recyclerView.adapter = PlayersAdapter().apply {
                    //                    submitList(it.)
                }
            })
        }
    }
}

class PlayersAdapter : SimpleListAdapter2<PlayerData>(R.layout.vh_player, itemCallback()) {

    override fun onBindViewHolder(item: PlayerData, view: View) {
        view.name.text = "${item.name} ${item.surname}"
    }
}


val teamDetailsModule = module {
    viewModel { TeamDetailsViewModel() }
}