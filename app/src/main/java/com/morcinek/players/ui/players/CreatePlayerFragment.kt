package com.morcinek.players.ui.players

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.map
import com.morcinek.players.core.database.observe
import com.morcinek.players.core.database.teamsLiveDataForSingleValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.core.ui.showStandardDropDown
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_create_player.view.*
import kotlinx.android.synthetic.main.header_button.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module
import java.util.*

class CreatePlayerFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_create_player

    private val viewModel by viewModel<CreatePlayerViewModel>()

    private val navController: NavController by lazyNavController()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.nameTextInputLayout.editText?.doOnTextChanged { text, _, _, _ -> viewModel.updateValue { name = text.toString() } }
        view.surnameTextInputLayout.editText?.doOnTextChanged { text, _, _, _ -> viewModel.updateValue { surname = text.toString() } }
        view.birthDateLayout.apply {
            header.setText(R.string.birth_date)
            value.setText(R.string.value_not_set)
            setOnClickListener {
                startDatePicker(viewModel.dateInMillis()) {
                    viewModel.updateValue { birthDateInMillis = it.timeInMillis }
                    value.text = it.toSimpleString()
                }
            }
        }
        view.teamLayout.apply {
            header.setText(R.string.team)
            value.setText(R.string.value_not_set)
            setOnClickListener {
                viewModel.teams.observe(this@CreatePlayerFragment) {
                    showStandardDropDown(android.R.layout.simple_dropdown_item_1line, it) {
                        viewModel.updateValue { teamKey = it.key }
                        value.text = it.name
                    }
                }
            }
        }
        view.nextButton.apply {
            viewModel.isNextEnabled.observe(this@CreatePlayerFragment, Observer { isEnabled = it })
            setOnClickListener {
                viewModel.createPlayer { navController.popBackStack() }
            }
        }
    }

    private fun startDatePicker(timeInMillis: Long, updatedDate: (Calendar) -> Unit) =
        showYearFirstDatePickerDialog(calendar(timeInMillis) ?: Calendar.getInstance().apply { year = 2009 }, updatedDate)
}

val createPlayerModule = module {
    viewModel { CreatePlayerViewModel(get()) }
}

private val DefaultDate = Calendar.getInstance().apply { year = 2009 }.timeInMillis

private class CreatePlayerViewModel(val references: FirebaseReferences) : ViewModel() {

    val teams = references.teamsLiveDataForSingleValueListener()

    val player: LiveData<PlayerData> = MutableLiveData<PlayerData>().apply { value = PlayerData() }

    fun dateInMillis() = player.value!!.birthDateInMillis.takeIf { it > 0 } ?: DefaultDate

    val isNextEnabled: LiveData<Boolean> = player.map { it.isValid() }

    fun updateValue(function: PlayerData.() -> Unit) {
        (player as MutableLiveData).postValue(player.value?.apply(function))
    }

    fun createPlayer(doOnComplete: () -> Unit) = references.playersReference().push().setValue(player.value).addOnSuccessListener { doOnComplete() }
}

private fun PlayerData.isValid() = name.isNotBlank() && surname.isNotBlank() && birthDateInMillis != 0L
