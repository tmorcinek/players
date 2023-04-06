package com.morcinek.players.ui.team

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.core.lazyNavController
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.playersWithoutTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.getParcelableOrNull
import com.morcinek.players.core.extensions.map
import com.morcinek.players.core.extensions.observe
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.databinding.FragmentCreateTeamBinding
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class CreateTeamFragment : BaseFragment<FragmentCreateTeamBinding>(FragmentCreateTeamBinding::inflate) {

    override val title = R.string.menu_create_team

    private val viewModel by viewModelWithFragment<CreateTeamViewModel>()

    private val navController by lazyNavController()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            nameTextInputLayout.editText?.run {
                setText(viewModel.teamData?.name)
                doOnTextChanged { text, _, _, _ -> viewModel.updateValue { name = text.toString() } }
            }
            recyclerView.run {
                layoutManager = LinearLayoutManager(activity)
//                adapter = SelectionListAdapter<PlayerData>(R.layout.vh_selectable_player, itemCallback()) { _, item ->
//                    name.text = "$item"
//                }.apply {
//                    selectedItems = viewModel.selectedPlayers
//                    observe(viewModel.players) { submitList(it) }
//                    onSelectedItemsChanged { viewModel.selectedPlayers = it }
//                }
            }
            nextButton.run {
                observe(viewModel.isNextEnabled) { isEnabled = it }
                setOnClickListener { viewModel.createOrUpdateTeam { navController.popBackStack() } }
            }
        }
    }
}

private class CreateTeamViewModel(val references: FirebaseReferences, val teamData: TeamData?) : ViewModel() {

    private val team = MutableLiveData(TeamData())

    var selectedPlayers = setOf<PlayerData>()

    val isNextEnabled = team.map { it.name.isNotBlank() }

    fun updateValue(function: TeamData.() -> Unit) = team.postValue(team.value?.apply(function))

    fun createOrUpdateTeam(doOnComplete: () -> Unit) {
        val childUpdates = HashMap<String, Any>()
        references.teamsReference().push().key!!.let { key ->
            childUpdates["/teams/$key"] = team.value!!.toMap()
            selectedPlayers.forEach {
                childUpdates["/players/${it.key}/teamKey"] = key
            }
        }
        references.rootReference().updateChildren(childUpdates).addOnCompleteListener { doOnComplete() }
    }

    val players = references.playersWithoutTeamLiveDataForValueListener()
}

val createTeamModule = module {
    viewModel { (fragment: Fragment) -> CreateTeamViewModel(get(), fragment.getParcelableOrNull()) }
}
