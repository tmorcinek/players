package com.morcinek.players.ui.teams

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import kotlinx.android.synthetic.main.fragment_teams.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class TeamsFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_teams

    private val viewModel by viewModel<TeamsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.text.observe(this, Observer {
            view.text.text = it.name
        })
    }
}

val teamsModule = module {
    viewModel { TeamsViewModel() }
}