package com.morcinek.players.ui.teams

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import kotlinx.android.synthetic.main.fragment_list.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class TeamsFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_list

    private val viewModel by viewModel<TeamsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.layoutAnimation = LayoutAnimationController(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in))
            recyclerView.adapter = TeamsAdapter().apply {
                viewModel.teams.observe(this@TeamsFragment, Observer { submitList(it) })
            }
        }

        viewModel.teams.observe(this, Observer {

        })
    }
}

val teamsModule = module {
    viewModel { TeamsViewModel() }
}