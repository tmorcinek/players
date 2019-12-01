package com.morcinek.players.core.extensions

import android.os.Bundle
import android.view.MenuItem
import android.view.SubMenu
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions

fun SubMenu.add(title: String, icon: Int, onMenuItemClick: (MenuItem) -> Unit): MenuItem =
    add(title).setIcon(icon).setOnMenuItemClickListener {
        onMenuItemClick(it)
        true
    }

fun NavController.navigateSingleTop(@IdRes resId: Int, args: Bundle? = null) {
    navigate(resId, args, NavOptions.Builder().setLaunchSingleTop(true).build())
}