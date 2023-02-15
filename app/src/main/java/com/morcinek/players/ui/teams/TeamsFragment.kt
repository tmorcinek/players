package com.morcinek.players.ui.teams

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import com.morcinek.android.itemCallback
import com.morcinek.android.list
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createFabConfiguration
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.teamsLiveDataForValueListener
import com.morcinek.players.core.extensions.bundle
import com.morcinek.players.databinding.FragmentListBinding
import com.morcinek.players.databinding.VhTeamBinding
import com.morcinek.players.ui.lazyNavController
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class TeamsFragment : BaseFragment<FragmentListBinding>(FragmentListBinding::inflate) {

    private val viewModel by viewModel<TeamsViewModel>()

    private val navController by lazyNavController()

    override val fabConfiguration = createFabConfiguration(R.drawable.ic_group_add) { navController.navigate(R.id.nav_create_team) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            progressBar.show()
            recyclerView.list(itemCallback<TeamData>(), VhTeamBinding::inflate) {
                onBind { _, item ->
                    name.text = item.name
                    delete.setOnClickListener {
//                        navController.navigate(R.id.nav_team_details, item.toBundleWithTitle { name })
                    }
                    edit.setOnClickListener { navController.navigate(R.id.action_nav_teams_to_team_info, bundle(item)) }
                }
                liveData(viewLifecycleOwner, viewModel.teams) { progressBar.hide() }
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