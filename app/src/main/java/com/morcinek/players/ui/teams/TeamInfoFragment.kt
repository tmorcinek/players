package com.morcinek.players.ui.teams

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.morcinek.android.itemCallback
import com.morcinek.android.list
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.toYearString
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.databinding.FragmentTeamInfoBinding
import com.morcinek.players.databinding.VhPlayerInfoBinding
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class TeamInfoFragment : BaseFragment<FragmentTeamInfoBinding>(FragmentTeamInfoBinding::inflate) {

    private val viewModel by viewModelWithFragment<TeamInfoViewModel>()

    override val menuConfiguration = createMenuConfiguration {
        addAction(R.string.add_players, R.drawable.ic_person_add) {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            name.text = viewModel.teamData.name
            recyclerView.list(itemCallback<PlayerData>(), VhPlayerInfoBinding::inflate) {
                onBind { _, item ->
                    name.text = item.toString()
                    date.text = item.getBirthDate().toYearString()
                }
                liveData(viewLifecycleOwner, viewModel.players)
            }
        }
    }
}

private class TeamInfoViewModel(references: FirebaseReferences, val teamData: TeamData) : ViewModel() {

    val players = references.playersForTeamLiveDataForValueListener(teamData.key)
}

val teamInfoModule = module {
    viewModel { (fragment: Fragment) -> TeamInfoViewModel(get(), fragment.getParcelable()) }
}
