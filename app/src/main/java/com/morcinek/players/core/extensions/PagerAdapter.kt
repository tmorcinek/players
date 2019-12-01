package com.morcinek.players.core.extensions

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter


fun Fragment.recyclerViewPagerAdapter(vararg pagers: Pair<String, RecyclerView.Adapter<out RecyclerView.ViewHolder>>): PagerAdapter = object : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    override fun getCount() = pagers.size

    override fun getPageTitle(position: Int) = pagers[position].first

    override fun instantiateItem(container: ViewGroup, position: Int) = RecyclerView(requireContext()).apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = pagers[position].second
        container.addView(this)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as View)
}