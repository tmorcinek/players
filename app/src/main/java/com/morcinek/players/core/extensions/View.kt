package com.morcinek.players.core.extensions

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes

fun View.setDrawableColor(color: Int) {
    (background as GradientDrawable).setColor(color)
}

fun LayoutInflater.inflate(@LayoutRes resource: Int): View = inflate(resource, null, false)

fun Context.inflate(@LayoutRes resource: Int) = LayoutInflater.from(this).inflate(resource)


fun numericKeyboardTransformationMethod() = object : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View) = source
}
