package com.morcinek.players.core.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent


inline fun <reified T : Activity> Context.createIntent(function: Intent.() -> Unit = {}) = Intent(this, T::class.java).apply(function)

inline fun <reified T : Activity> Activity.startNewActivityFinishCurrent() {
    startActivity<T>()
    finish()
}

inline fun <reified T : Activity> Context.startActivity(function: Intent.() -> Unit = {}) = startActivity(createIntent<T>(function))

fun Activity.startActivityForResult(intent: Intent, requestCode: Int = 0) = startActivityForResult(intent, requestCode)
inline fun <reified T : Activity> Activity.startActivityForResult(requestCode: Int = 0) = startActivityForResult(createIntent<T>(), requestCode)
