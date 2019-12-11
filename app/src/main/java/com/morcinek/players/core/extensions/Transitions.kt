package com.morcinek.players.core.extensions

import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigator
import androidx.transition.TransitionInflater


fun Fragment.moveTransition() = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)

fun FragmentNavigatorExtras(vararg sharedElements: View) =
    FragmentNavigator.Extras.Builder().apply {
        sharedElements.forEach {
            addSharedElement(it, ViewCompat.getTransitionName(it)!!)
        }
    }.build()
