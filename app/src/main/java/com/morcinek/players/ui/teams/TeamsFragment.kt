package com.morcinek.players.ui.teams

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.ClickableListAdapter
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.itemCallback
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.vh_player.view.*
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
            recyclerView.adapter = TeamsAdapter().apply {
                viewModel.teams.observe(this@TeamsFragment, Observer { submitList(it) })
                onClickListener { view, teamData ->

                }
            }
        }
    }
}

private class TeamsAdapter : ClickableListAdapter<TeamData>(R.layout.vh_player, itemCallback()) {

    override fun onBindViewHolder(item: TeamData, view: View) {
        super.onBindViewHolder(item, view)
        view.name.text = "${item.name}/${item.category}"
    }
}

val teamsModule = module {
    viewModel { TeamsViewModel(get()) }
}