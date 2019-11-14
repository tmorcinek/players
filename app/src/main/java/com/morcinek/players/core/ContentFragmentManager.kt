package com.morcinek.players.core

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.morcinek.players.R

class ContentFragmentManager(private val activity: FragmentActivity) {

    private val fragmentManager by lazy { activity.supportFragmentManager }

    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = false, tag: String = fragment.javaClass.name,
                        transaction: FragmentTransaction.() -> Unit = {}) {
        fragmentManager.beginTransaction().apply {
            transaction()
            replace(R.id.fragmentContainer, fragment, tag)
            if (addToBackStack) {
                addToBackStack(tag)
            }
            commit()
        }
    }
}