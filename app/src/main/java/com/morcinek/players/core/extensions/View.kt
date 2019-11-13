package com.morcinek.players.core.extensions

import android.graphics.drawable.GradientDrawable
import android.view.View

fun View.setDrawableColor(color: Int) {
    (background as GradientDrawable).setColor(color)
}
