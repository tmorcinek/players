package com.morcinek.players.ui.teams

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.observe
import com.morcinek.players.core.database.playersWithoutTeamLiveDataForValueListener
import com.morcinek.players.ui.funino.create.WhichPlayersAdapter
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_create_player.view.*
import kotlinx.android.synthetic.main.fragment_create_player.view.nextButton
import kotlinx.android.synthetic.main.fragment_which_players.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class CreateTeamFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_create_team

    private val viewModel by viewModel<CreateTeamViewModel>()

    private val navController: NavController by lazyNavController()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.nameTextInputLayout.editText?.doOnTextChanged { text, _, _, _ -> viewModel.updateValue { name = text.toString() } }
        view.nextButton.apply {
            viewModel.isNextEnabled.observe(this@CreateTeamFragment) { isEnabled = it }
            setOnClickListener {
                viewModel.createTeam { navController.popBackStack() }
            }
        }
        view.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = WhichPlayersAdapter().apply {
                viewModel.players.observe(this@CreateTeamFragment) { submitList(it) }
                viewModel.selectedPlayers.observe(this@CreateTeamFragment) { selectedItems = it }
                onClickListener { _, item -> viewModel.select(item) }
            }
        }
    }
}

val createTeamModule = module {
    viewModel { CreateTeamViewModel(get()) }
}

private class CreateTeamViewModel(val references: FirebaseReferences) : ViewModel() {

    val team: LiveData<TeamData> = MutableLiveData<TeamData>().apply { value = TeamData() }

    val isNextEnabled: LiveData<Boolean> = Transformations.map(team) { it.isValid() }

    fun updateValue(function: TeamData.() -> Unit) = (team as MutableLiveData).postValue(team.value?.apply(function))

    fun createTeam(doOnComplete: () -> Unit) {
        val key = references.teamsReference().push().key!!
        val childUpdates = HashMap<String, Any>()
        childUpdates["/teams/$key"] = team.value!!.toMap()
        selectedPlayers.value?.forEach {
            childUpdates["/players/${it.key}/teamKey"] = key
        }
        references.rootReference().updateChildren(childUpdates).addOnCompleteListener { doOnComplete() }
    }


    val players = references.playersWithoutTeamLiveDataForValueListener()

    val selectedPlayers: LiveData<Set<PlayerData>> = MutableLiveData<Set<PlayerData>>().apply { value = setOf() }

    fun select(player: PlayerData) = (selectedPlayers as MutableLiveData).postValue(updateSelectedPlayer(player))

    private fun updateSelectedPlayer(color: PlayerData) = selectedPlayers.value!!.let { selectedColors ->
        if (color in selectedColors) selectedColors.minus(color) else selectedColors.plus(color)
    }
}

private fun TeamData.isValid() = name.isNotBlank()