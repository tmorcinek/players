package com.morcinek.players.core.extensions.alert

import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

const val NO_GETTER: String = "Property does not have a getter"
fun noGetter(): Nothing = throw Exception("Property does not have a getter")

internal class AndroidAlertBuilder(override val ctx: Context) : AlertBuilder<AlertDialog> {
    private val builder = AlertDialog.Builder(ctx)

    override var title: CharSequence
        @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
        set(value) {
            builder.setTitle(value)
        }

    override var titleResource: Int
        @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
        set(value) {
            builder.setTitle(value)
        }

    override var message: CharSequence
        @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
        set(value) {
            builder.setMessage(value)
        }

    override var messageResource: Int
        @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
        set(value) {
            builder.setMessage(value)
        }

    override var isCancelable: Boolean
        @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
        set(value) {
            builder.setCancelable(value)
        }

    override fun onCancelled(handler: (DialogInterface) -> Unit) {
        builder.setOnCancelListener(handler)
    }

    override fun positiveButton(buttonText: String, onClicked: (dialog: DialogInterface) -> Unit) {
        builder.setPositiveButton(buttonText) { dialog, _ -> onClicked(dialog) }
    }

    override fun positiveButton(buttonTextResource: Int, onClicked: (dialog: DialogInterface) -> Unit) {
        builder.setPositiveButton(buttonTextResource) { dialog, _ -> onClicked(dialog) }
    }

    override fun negativeButton(buttonText: String, onClicked: (dialog: DialogInterface) -> Unit) {
        builder.setNegativeButton(buttonText) { dialog, _ -> onClicked(dialog) }
    }

    override fun negativeButton(buttonTextResource: Int, onClicked: (dialog: DialogInterface) -> Unit) {
        builder.setNegativeButton(buttonTextResource) { dialog, _ -> onClicked(dialog) }
    }

    override fun neutralButton(buttonText: String, onClicked: (dialog: DialogInterface) -> Unit) {
        builder.setNeutralButton(buttonText) { dialog, _ -> onClicked(dialog) }
    }

    override fun neutralButton(buttonTextResource: Int, onClicked: (dialog: DialogInterface) -> Unit) {
        builder.setNeutralButton(buttonTextResource) { dialog, _ -> onClicked(dialog) }
    }

    override fun build(): AlertDialog = builder.create()

    override fun show(): AlertDialog = builder.show()
}