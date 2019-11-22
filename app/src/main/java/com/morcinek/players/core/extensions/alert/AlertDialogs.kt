package com.morcinek.players.core.extensions.alert

import android.content.Context
import android.content.DialogInterface
import androidx.fragment.app.Fragment

fun Context.alert(
    messageResource: Int,
    titleResource: Int? = null,
    isCancelable: Boolean = true,
    init: (AlertBuilder<DialogInterface>.() -> Unit)? = null
): AlertBuilder<DialogInterface> = AndroidAlertBuilder(this).apply {
    if (titleResource != null) {
        this.titleResource = titleResource
    }
    this.messageResource = messageResource
    this.isCancelable = isCancelable
    if (init != null) init()
}

fun Context.alert(
    message: String,
    titleResource: Int? = null,
    isCancelable: Boolean = true,
    init: (AlertBuilder<DialogInterface>.() -> Unit)? = null
): AlertBuilder<DialogInterface> = AndroidAlertBuilder(this).apply {
    if (titleResource != null) {
        this.titleResource = titleResource
    }
    this.message = message
    this.isCancelable = isCancelable
    if (init != null) init()
}

fun Context.alertNonCancelable(
    messageResource: Int,
    titleResource: Int? = null,
    init: (AlertBuilder<DialogInterface>.() -> Unit)? = null
) = alert(messageResource, titleResource, false, init)

fun Fragment.alert(
    messageResource: Int,
    titleResource: Int? = null,
    isCancelable: Boolean = true,
    init: (AlertBuilder<DialogInterface>.() -> Unit)? = null
) = requireContext().alert(messageResource, titleResource, isCancelable, init)

fun Fragment.alert(
    message: String,
    titleResource: Int? = null,
    isCancelable: Boolean = true,
    init: (AlertBuilder<DialogInterface>.() -> Unit)? = null
) = requireContext().alert(message, titleResource, isCancelable, init)


fun Fragment.alertNonCancelable(messageRes: Int, titleRes: Int? = null, init: (AlertBuilder<DialogInterface>.() -> Unit)? = null) =
    alert(messageRes, titleRes, false, init)

fun Fragment.alertNonCancelable(message: String, titleRes: Int? = null, init: (AlertBuilder<DialogInterface>.() -> Unit)? = null) =
    alert(message, titleRes, false, init)
