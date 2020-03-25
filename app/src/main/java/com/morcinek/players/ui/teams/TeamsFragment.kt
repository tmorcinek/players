package com.morcinek.players.ui.teams

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createFabConfiguration
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.teamsLiveDataForValueListener
import com.morcinek.players.core.extensions.observe
import com.morcinek.players.core.extensions.toBundleWithTitle
import com.morcinek.players.core.itemCallback
import com.morcinek.players.core.listAdapter
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.vh_team.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class TeamsFragment : BaseFragment(R.layout.fragment_list) {

    private val viewModel by viewModel<TeamsViewModel>()

    private val navController: NavController by lazyNavController()

    override val fabConfiguration = createFabConfiguration(R.drawable.ic_group_add) { navController.navigate(R.id.nav_create_team) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            progressBar.show()
            recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = listAdapter<TeamData>(R.layout.vh_team, itemCallback()) { _, item ->
                    name.text = item.name
                    setOnClickListener { navController.navigate(R.id.nav_team_details, item.toBundleWithTitle { name }) }
                }.apply {
                    observe(viewModel.teams) {
                        submitList(it)
                        view.progressBar.hide()
                    }
                }
            }
        }
    }
}

private class TeamsViewModel(references: FirebaseReferences) : ViewModel() {

    val teams = references.teamsLiveDataForValueListener()
}

val teamsModule = module {
    viewModel { TeamsViewModel(get()) }
}