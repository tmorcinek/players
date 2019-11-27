package com.morcinek.players.core.extensions

import com.google.android.material.floatingactionbutton.FloatingActionButton

inline fun FloatingActionButton.hide(crossinline onHidden: (FloatingActionButton?) -> Unit) {
    hide(object : FloatingActionButton.OnVisibilityChangedListener() {
        override fun onHidden(fab: FloatingActionButton?) = onHidden(fab)
    })
}
