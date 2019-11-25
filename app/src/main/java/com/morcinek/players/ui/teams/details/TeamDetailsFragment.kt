package com.morcinek.players.ui.teams.details

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.itemCallback
import com.morcinek.players.core.simpleListAdapter
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
//                category.text = it.category
//                year.text = it.year.toString()
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.adapter = simpleListAdapter<PlayerData>(R.layout.vh_player, itemCallback()) { item, view ->
                    view.name.text = item.toString()
                }.apply {
                    //                    submitList(it.)
                }
            })
        }
    }
}

val teamDetailsModule = module {
    viewModel { TeamDetailsViewModel() }
}

class TeamDetailsViewModel : ViewModel() {

    val team: LiveData<TeamData> = MutableLiveData<TeamData>().apply {
        value = TeamData("key", "Skrzaty 2019")
    }
}