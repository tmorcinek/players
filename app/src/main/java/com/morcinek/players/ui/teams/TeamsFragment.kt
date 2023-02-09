package com.morcinek.players.ui.teams

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createFabConfiguration
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.teamsLiveDataForValueListener
import com.morcinek.players.core.extensions.bundle
import com.morcinek.players.core.extensions.toBundleWithTitle
import com.morcinek.players.core.itemCallback
import com.morcinek.players.ui.lazyNavController
import com.morcinek.recyclerview.list
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.vh_team.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class TeamsFragment : BaseFragment(R.layout.fragment_list) {

    private val viewModel by viewModel<TeamsViewModel>()

    private val navController by lazyNavController()

    override val fabConfiguration = createFabConfiguration(R.drawable.ic_group_add) { navController.navigate(R.id.nav_create_team) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            progressBar.show()
            recyclerView.list<TeamData>(itemCallback()) {
                resId(R.layout.vh_team)
                onBind { _, item ->
                    name.text = item.name
                    delete.setOnClickListener {
//                        navController.navigate(R.id.nav_team_details, item.toBundleWithTitle { name })
                    }
                    edit.setOnClickListener { navController.navigate(R.id.action_nav_teams_to_team_info, bundle(item)) }
                }
                liveData(viewLifecycleOwner, viewModel.teams) { view.progressBar.hide() }
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