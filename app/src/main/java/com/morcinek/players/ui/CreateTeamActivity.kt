package com.morcinek.players.ui

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.extensions.map
import com.morcinek.players.core.extensions.observe
import com.morcinek.players.databinding.ActivityCreateTeamBinding
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class CreateTeamActivity : AppCompatActivity() {

    private val viewModel by viewModel<CreateTeamViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCreateTeamBinding.inflate(layoutInflater).run {
            setContentView(root)
            nameTextInputLayout.editText?.run {
                setText("")
                doOnTextChanged { text, _, _, _ -> viewModel.updateValue { name = text.toString() } }
            }
            nextButton.run {
                observe(viewModel.isNextEnabled) { isEnabled = it }
                setOnClickListener {
                    viewModel.createOrUpdateTeam {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }
        }
    }
}


private class CreateTeamViewModel(val references: FirebaseReferences) : ViewModel() {

    private val team = MutableLiveData(TeamData())

    val isNextEnabled = team.map { it.name.isNotBlank() }

    fun updateValue(function: TeamData.() -> Unit) = team.postValue(team.value?.apply(function))

    fun createOrUpdateTeam(doOnComplete: () -> Unit) {
        val childUpdates = HashMap<String, Any>()
        references.teamsReference().push().key!!.let { key ->
            childUpdates["/teams/$key"] = team.value!!.toMap()
        }
        references.rootReference().updateChildren(childUpdates).addOnCompleteListener { doOnComplete() }
    }
}

val createFirstTeamModule = module {
    viewModel { CreateTeamViewModel(get()) }
}
