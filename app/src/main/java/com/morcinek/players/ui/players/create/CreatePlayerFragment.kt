package com.morcinek.players.ui.players.create

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.extensions.calendar
import com.morcinek.players.core.extensions.showDatePickerDialog
import com.morcinek.players.core.extensions.toStandardString
import com.morcinek.players.core.extensions.year
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_create_player.view.*
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
        view.birthDateButton.setOnClickListener {
            startDatePicker(viewModel.createPlayer.value?.birthDateInMillis ?: 0) {
                viewModel.updateValue { birthDateInMillis = it.timeInMillis }
                view.birthDateButton.text = it.toStandardString()
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
        requireContext().showDatePickerDialog(calendar(timeInMillis) ?: Calendar.getInstance().apply { year = 2009 }, updatedDate)
}

val createPlayerModule = module {
    viewModel { CreatePlayerViewModel(get()) }
}

class CreatePlayerViewModel(val references: FirebaseReferences) : ViewModel() {

    val createPlayer: LiveData<CreatePlayer> = MutableLiveData<CreatePlayer>().apply { value = CreatePlayer() }

    val isNextEnabled: LiveData<Boolean> = Transformations.map(createPlayer) { it.isValid() }

    fun updateValue(function: CreatePlayer.() -> Unit) {
        (createPlayer as MutableLiveData).postValue(createPlayer.value?.apply(function))
    }

    fun createPlayer(doOnComplete: () -> Unit) = references.playersReference().push().setValue(createPlayer.value).addOnSuccessListener { doOnComplete() }
}

class CreatePlayer(
    var name: String = "",
    var surname: String = "",
    var birthDateInMillis: Long = 0L
) {

    fun isValid() = name.isNotBlank() && surname.isNotBlank() && birthDateInMillis != 0L
}