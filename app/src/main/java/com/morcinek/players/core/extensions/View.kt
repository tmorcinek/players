package com.morcinek.players.core.extensions

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

fun View.setDrawableColor(color: Int) {
    (background as GradientDrawable).setColor(color)
}

fun LayoutInflater.inflate(@LayoutRes resource: Int): View = inflate(resource, null, false)

fun Context.inflate(@LayoutRes resource: Int) = LayoutInflater.from(this).inflate(resource)


fun numericKeyboardTransformationMethod() = object : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View) = source
}

fun Fragment.toast(text: String) = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
