package com.morcinek.players.ui.teams

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.SelectListAdapter
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.*
import com.morcinek.players.core.itemCallback
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_create_player.view.*
import kotlinx.android.synthetic.main.fragment_create_player.view.nextButton
import kotlinx.android.synthetic.main.fragment_which_players.view.*
import kotlinx.android.synthetic.main.vh_selectable_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class CreateTeamFragment : BaseFragment(R.layout.fragment_create_team) {

    private val viewModel by viewModel<CreateTeamViewModel>()

    private val navController: NavController by lazyNavController()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            nameTextInputLayout.editText?.doOnTextChanged { text, _, _, _ -> viewModel.updateValue { name = text.toString() } }
            recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = SelectListAdapter<PlayerData>(R.layout.vh_selectable_player, itemCallback()) { _, item ->
                    name.text = "$item"
                }.apply {
                    observe(viewModel.players) { submitList(it) }
                    viewModel.selectedPlayers.setSingleSource(selectedItems)
                }
            }
            nextButton.apply {
                observe(viewModel.isNextEnabled) { isEnabled = it }
                setOnClickListener {
                    viewModel.createTeam { navController.popBackStack() }
                }
            }
        }
    }
}

private class CreateTeamViewModel(val references: FirebaseReferences) : ViewModel() {

    val team = valueLiveData(TeamData())

    val isNextEnabled: LiveData<Boolean> = team.map { it.isValid() }

    fun updateValue(function: TeamData.() -> Unit) = (team as MutableLiveData).postValue(team.value?.apply(function))

    fun createTeam(doOnComplete: () -> Unit) {
        val childUpdates = HashMap<String, Any>()
        references.teamsReference().push().key!!.let { key ->
            childUpdates["/teams/$key"] = team.value!!.toMap()
            selectedPlayers.value?.forEach {
                childUpdates["/players/${it.key}/teamKey"] = key
            }
        }
        references.rootReference().updateChildren(childUpdates).addOnCompleteListener { doOnComplete() }
    }

    val players = references.playersWithoutTeamLiveDataForValueListener()

    val selectedPlayers = SingleSourceMediator<Set<PlayerData>>()
}

private fun TeamData.isValid() = name.isNotBlank()

val createTeamModule = module {
    viewModel { CreateTeamViewModel(get()) }
}
