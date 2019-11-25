package com.morcinek.players.ui.teams

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.clickableListAdapter
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.observe
import com.morcinek.players.core.database.teamsLiveDataForValueListener
import com.morcinek.players.core.itemCallback
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.vh_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class TeamsFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_list

    private val viewModel by viewModel<TeamsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.progressBar.show()
        view.apply {
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = clickableListAdapter<TeamData>(R.layout.vh_player, itemCallback()) { item, view ->
                view.name.text = "${item.name}/${item.category}"
            }.apply {
                viewModel.teams.observe(this@TeamsFragment) {
                    submitList(it)
                    view.progressBar.hide()
                }
                onClickListener { view, teamData ->

                }
            }
        }
    }
}

class TeamsViewModel(references: FirebaseReferences) : ViewModel() {

    val teams = references.teamsLiveDataForValueListener()
}

val teamsModule = module {
    viewModel { TeamsViewModel(get()) }
}