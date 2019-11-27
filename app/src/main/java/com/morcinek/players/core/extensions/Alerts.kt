package com.morcinek.players.core.extensions

import androidx.fragment.app.Fragment
import com.morcinek.players.core.extensions.alert.alert
import com.morcinek.players.core.extensions.alert.okButton

fun Fragment.showOkAlert(message: Int, titleRes: Int? = null) =
    alert(message, titleRes, false) { okButton {  }}.show()
