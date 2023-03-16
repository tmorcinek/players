package com.morcinek.core.nav

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.StringRes
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import com.morcinek.players.R

internal abstract class AbstractAppBarOnDestinationChangedListener(private val context: Context) {

    private var arrowDrawable: DrawerArrowDrawable? = null
    private var animator: ValueAnimator? = null

    protected abstract fun setTitle(title: CharSequence?)

    protected abstract fun setNavigationIcon(icon: Drawable?, @StringRes contentDescription: Int)

    @SuppressLint("ObjectAnimatorBinding")
    private fun setActionBarUpIndicator(showAsDrawerIndicator: Boolean) {
        val (arrow, animate) = arrowDrawable?.run {
            this to true
        } ?: (DrawerArrowDrawable(context).also { arrowDrawable = it } to false)

        setNavigationIcon(
            arrow,
            if (showAsDrawerIndicator) R.string.navigation_drawer_open
            else R.string.navigation_drawer_close
        )

        val endValue = if (showAsDrawerIndicator) 0f else 1f
        if (animate) {
            val startValue = arrow.progress
            animator?.cancel()
            animator = ObjectAnimator.ofFloat(arrow, "progress", startValue, endValue)
            (animator as ObjectAnimator).start()
        } else {
            arrow.progress = endValue
        }
    }
}
