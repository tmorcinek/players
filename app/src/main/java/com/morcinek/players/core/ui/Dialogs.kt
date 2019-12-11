package com.morcinek.players.core.ui

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.morcinek.players.R
import com.morcinek.players.core.extensions.alert.alert
import com.morcinek.players.core.extensions.alert.noButton
import com.morcinek.players.core.extensions.alert.yesButton
import com.morcinek.players.core.extensions.combineTwo
import com.morcinek.players.core.extensions.inflate
import com.morcinek.players.core.extensions.numericKeyboardTransformationMethod
import kotlinx.android.synthetic.main.dialog_code_confirmation.view.*
import kotlin.random.Random


fun Fragment.showCodeConfirmationDialog(message: Int, onSuccess: () -> Unit) = requireContext().showCodeConfirmationDialog(message, onSuccess)

fun Context.showCodeConfirmationDialog(message: Int, onSuccess: () -> Unit) =
    combineTwo(Random.Default.nextInt(1000, 9999).toString(), inflate(R.layout.dialog_code_confirmation)) { code, view ->
        AlertDialog.Builder(this)
            .setMessage(message)
            .setView(view)
            .create().apply {
                view.apply {
                    value.text = code
                    codeTextInputLayout.editText?.transformationMethod = numericKeyboardTransformationMethod()
                    codeTextInputLayout.editText?.doOnTextChanged { text, _, _, _ -> confirmButton.isEnabled = text.toString() == code }
                    confirmButton.setOnClickListener {
                        onSuccess()
                        dismiss()
                    }
                }
            }.show()
    }


fun Fragment.showDeleteCodeConfirmationDialog(query: Int, message: Int, onSuccess: () -> Unit) =
    alert(query) {
        yesButton { showCodeConfirmationDialog(message, onSuccess) }
        noButton {}
    }.show()
