package com.morcinek.players.core.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.PagerAdapter


fun <B : ViewBinding> Fragment.singlePageAdapter(createBinding: (LayoutInflater, ViewGroup?, Boolean) -> B, initialization: B.() -> Unit): PagerAdapter = object : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    override fun getCount() = 1

    override fun instantiateItem(container: ViewGroup, position: Int) =
        createBinding(layoutInflater, container, false).apply(initialization).also { container.addView(it.root) }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as View)
}