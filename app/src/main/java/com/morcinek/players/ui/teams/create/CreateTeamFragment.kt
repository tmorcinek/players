package com.morcinek.players.ui.teams.create

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.observe
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_create_player.view.*
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
    }
}

val createTeamModule = module {
    viewModel { CreateTeamViewModel(get()) }
}

private class CreateTeamViewModel(val references: FirebaseReferences) : ViewModel() {

    val team: LiveData<TeamData> = MutableLiveData<TeamData>().apply { value = TeamData() }

    val isNextEnabled: LiveData<Boolean> = Transformations.map(team) { it.isValid() }

    fun updateValue(function: TeamData.() -> Unit) = (team as MutableLiveData).postValue(team.value?.apply(function))

    fun createTeam(doOnComplete: () -> Unit) = references.teamsReference().push().setValue(team.value).addOnSuccessListener { doOnComplete() }
}

private fun TeamData.isValid() = name.isNotBlank()