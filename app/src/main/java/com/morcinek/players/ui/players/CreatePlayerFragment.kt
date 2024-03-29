package com.morcinek.players.ui.players

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morcinek.core.lazyNavController
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.teamsLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.core.ui.showStandardDropDown
import com.morcinek.players.databinding.FragmentCreatePlayerBinding
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*

class CreatePlayerFragment : BaseFragment<FragmentCreatePlayerBinding>(FragmentCreatePlayerBinding::inflate) {

    override val title : Int get() = getIntOrNull() ?: R.string.menu_create_player

    private val viewModel by viewModelWithFragment<CreatePlayerViewModel>()

    private val navController by lazyNavController()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            nameTextInputLayout.editText?.run {
                setText(viewModel.playerData.name)
                doOnTextChanged { text, _, _, _ -> viewModel.updateValue { name = text.toString() } }
            }
            surnameTextInputLayout.editText?.run {
                setText(viewModel.playerData.surname)
                doOnTextChanged { text, _, _, _ -> viewModel.updateValue { surname = text.toString() } }
            }
            birthDateLayout.run {
                value.text = viewModel.playerDate() ?: getString(R.string.value_not_set)
                header.setText(R.string.birth_date)
                root.setOnClickListener {
                    startDatePicker(viewModel.dateInMillis()) {
                        viewModel.updateValue { birthDateInMillis = it.timeInMillis }
                        value.text = it.toStandardString()
                    }
                }
            }
            teamLayout.run {
                header.setText(R.string.team)
                observe(viewModel.playerTeamName()) {
                    if (it == null) {
                        value.setText(R.string.value_not_set)
                        root.setOnClickListener {
                            observe(viewModel.teams) { teams ->
                                root.showStandardDropDown(android.R.layout.simple_dropdown_item_1line, teams) {
                                    viewModel.updateValue { teamKey = it.key }
                                    value.text = it.name
                                }
                            }
                        }
                    } else {
                        value.text = it
                    }
                }
            }
            nextButton.apply {
                observe(viewModel.isNextEnabled) { isEnabled = it }
                setOnClickListener { viewModel.createOrUpdatePlayer { navController.popBackStack() } }
            }
        }
    }

    private fun startDatePicker(timeInMillis: Long, updatedDate: (Calendar) -> Unit) =
        showYearFirstDatePickerDialog(calendar(timeInMillis) ?: Calendar.getInstance().apply { year = 2009 }, updatedDate)
}

private val DefaultDate = Calendar.getInstance().apply { year = 2009 }.timeInMillis

private class CreatePlayerViewModel(val references: FirebaseReferences, teamData: TeamData?, playerData: PlayerData?) : ViewModel() {

    private val player = MutableLiveData(playerData ?: PlayerData(teamKey = teamData?.key))

    val teams = references.teamsLiveDataForValueListener()

    val isNextEnabled = player.map { it.isValid() }

    val playerData: PlayerData = player.value!!

    fun playerDate() = playerData.takeIf { it.birthDateInMillis != 0L }?.getBirthDate()?.toStandardString()

    fun playerTeamName() = teams.map { it.find { it.key == playerData.teamKey }?.name }

    fun dateInMillis() = player.value!!.birthDateInMillis.takeIf { it > 0 } ?: DefaultDate

    fun updateValue(function: PlayerData.() -> Unit) = player.postValue(player.value?.apply(function))

    fun createOrUpdatePlayer(doOnComplete: () -> Unit) = if (playerData.key.isNotEmpty()) {
        references.playersReference().child(playerData.key).setValue(playerData)
    } else {
        references.playersReference().push().setValue(player.value)
    }.addOnSuccessListener { doOnComplete() }
}

private fun PlayerData.isValid() = name.isNotBlank() && surname.isNotBlank() && birthDateInMillis != 0L

val createPlayerModule = module {
    viewModel { (fragment: Fragment) -> CreatePlayerViewModel(get(), fragment.getParcelableOrNull(), fragment.getParcelableOrNull()) }
}
