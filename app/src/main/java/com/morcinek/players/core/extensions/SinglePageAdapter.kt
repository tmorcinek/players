package com.morcinek.players.core.extensions

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter


fun Fragment.singlePageAdapter(layoutId: Int, initialization: View.() -> Unit): PagerAdapter = object : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    override fun getCount() = 1

    override fun instantiateItem(container: ViewGroup, position: Int) =
        layoutInflater.inflate(layoutId, container, false).apply(initialization).also { container.addView(it) }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as View)
}