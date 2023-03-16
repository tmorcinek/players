package com.morcinek.core.nav

import android.graphics.drawable.Drawable
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity

internal class ActionBarOnDestinationChangedListener(
    private val activity: AppCompatActivity,
) : AbstractAppBarOnDestinationChangedListener(
    checkNotNull(activity.drawerToggleDelegate) {
        "Activity $activity does not have an DrawerToggleDelegate set"
    }.actionBarThemedContext,
) {
    override fun setTitle(title: CharSequence?) {
        val actionBar = checkNotNull(activity.supportActionBar) {
            "Activity $activity does not have an ActionBar set via setSupportActionBar()"
        }
        actionBar.title = title
    }

    override fun setNavigationIcon(icon: Drawable?, @StringRes contentDescription: Int) {
        val actionBar = checkNotNull(activity.supportActionBar) {
            "Activity $activity does not have an ActionBar set via setSupportActionBar()"
        }
        actionBar.setDisplayHomeAsUpEnabled(icon != null)
        val delegate = checkNotNull(activity.drawerToggleDelegate) {
            "Activity $activity does not have an DrawerToggleDelegate set"
        }
        delegate.setActionBarUpIndicator(icon, contentDescription)
    }
}
