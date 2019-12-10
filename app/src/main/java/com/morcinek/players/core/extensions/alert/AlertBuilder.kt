package com.morcinek.players.core.extensions.alert

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import kotlin.DeprecationLevel.ERROR

@SuppressLint("SupportAnnotationUsage")
interface AlertBuilder<out D : DialogInterface> {
    val ctx: Context

    var title: CharSequence
        @Deprecated(NO_GETTER, level = ERROR) get

    var titleResource: Int
        @Deprecated(NO_GETTER, level = ERROR) get

    var message: CharSequence
        @Deprecated(NO_GETTER, level = ERROR) get

    var messageResource: Int
        @Deprecated(NO_GETTER, level = ERROR) get

    var isCancelable: Boolean
        @Deprecated(NO_GETTER, level = ERROR) get

    var customView: Int
        @Deprecated(NO_GETTER, level = ERROR) get

    fun onCancelled(handler: (dialog: DialogInterface) -> Unit)

    fun positiveButton(buttonText: String, onClicked: (dialog: DialogInterface) -> Unit)
    fun positiveButton(@StringRes buttonTextResource: Int, onClicked: (dialog: DialogInterface) -> Unit)

    fun negativeButton(buttonText: String, onClicked: (dialog: DialogInterface) -> Unit)
    fun negativeButton(@StringRes buttonTextResource: Int, onClicked: (dialog: DialogInterface) -> Unit)

    fun neutralButton(buttonText: String, onClicked: (dialog: DialogInterface) -> Unit)
    fun neutralButton(@StringRes buttonTextResource: Int, onClicked: (dialog: DialogInterface) -> Unit)

    fun build(): D
    fun show(): D
}

inline fun AlertBuilder<*>.okButton(noinline handler: (dialog: DialogInterface) -> Unit) {
    positiveButton(android.R.string.ok, handler)
}

inline fun AlertBuilder<*>.cancelButton(noinline handler: (dialog: DialogInterface) -> Unit) {
    negativeButton(android.R.string.cancel, handler)
}

inline fun AlertBuilder<*>.yesButton(noinline handler: (dialog: DialogInterface) -> Unit) {
    positiveButton(android.R.string.yes, handler)
}

inline fun AlertBuilder<*>.noButton(noinline handler: (dialog: DialogInterface) -> Unit) {
    negativeButton(android.R.string.no, handler)
}