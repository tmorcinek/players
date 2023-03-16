package com.morcinek.players.core.extensions

import android.view.MenuItem
import android.view.SubMenu

fun SubMenu.add(title: String, icon: Int, onMenuItemClick: (MenuItem) -> Unit): MenuItem =
    add(title).setIcon(icon).setOnMenuItemClickListener {
        onMenuItemClick(it)
        true
    }
