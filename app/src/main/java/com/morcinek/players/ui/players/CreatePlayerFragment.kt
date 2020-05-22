package com.morcinek.players.ui.players

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.teamsLiveDataForSingleValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.core.ui.showStandardDropDown
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_create_player.view.*
import kotlinx.android.synthetic.main.header_button.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*

class CreatePlayerFragment : BaseFragment(R.layout.fragment_create_player) {

    private val viewModel by viewModelWithFragment<CreatePlayerViewModel>()

    private val navController by lazyNavController()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            nameTextInputLayout.editText?.doOnTextChanged { text, _, _, _ -> viewModel.updateValue { name = text.toString() } }
            surnameTextInputLayout.editText?.doOnTextChanged { text, _, _, _ -> viewModel.updateValue { surname = text.toString() } }
            birthDateLayout.apply {
                header.setText(R.string.birth_date)
                value.setText(R.string.value_not_set)
                setOnClickListener {
                    startDatePicker(viewModel.dateInMillis()) {
                        viewModel.updateValue { birthDateInMillis = it.timeInMillis }
                        value.text = it.toStandardString()
                    }
                }
            }
            teamLayout.apply {
                header.setText(R.string.team)
                if (viewModel.teamData != null) {
                    value.text = viewModel.teamData?.name
                } else {
                    value.setText(R.string.value_not_set)
                    setOnClickListener {
                        observe(viewModel.teams) { teams ->
                            showStandardDropDown(android.R.layout.simple_dropdown_item_1line, teams) {
                                viewModel.updateValue { teamKey = it.key }
                                value.text = it.name
                            }
                        }
                    }
                }
            }
            nextButton.apply {
                observe(viewModel.isNextEnabled) { isEnabled = it }
                setOnClickListener { viewModel.createPlayer { navController.popBackStack() } }
            }
        }
    }

    private fun startDatePicker(timeInMillis: Long, updatedDate: (Calendar) -> Unit) =
        showYearFirstDatePickerDialog(calendar(timeInMillis) ?: Calendar.getInstance().apply { year = 2009 }, updatedDate)
}

private val DefaultDate = Calendar.getInstance().apply { year = 2009 }.timeInMillis

private class CreatePlayerViewModel(val references: FirebaseReferences, val teamData: TeamData?) : ViewModel() {

    val teams = references.teamsLiveDataForSingleValueListener()

    private val player = MutableLiveData(PlayerData(teamKey = teamData?.key))

    val isNextEnabled = player.map { it.isValid() }

    fun dateInMillis() = player.value!!.birthDateInMillis.takeIf { it > 0 } ?: DefaultDate

    fun updateValue(function: PlayerData.() -> Unit) = player.postValue(player.value?.apply(function))

    fun createPlayer(doOnComplete: () -> Unit) = references.playersReference().push().setValue(player.value).addOnSuccessListener { doOnComplete() }
}

private fun PlayerData.isValid() = name.isNotBlank() && surname.isNotBlank() && birthDateInMillis != 0L

val createPlayerModule = module {
    viewModel { (fragment: Fragment) -> CreatePlayerViewModel(get(), fragment.getParcelableOrNull()) }
}
