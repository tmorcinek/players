package com.morcinek.recyclerview

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


fun Context.grid(
    spanCount: Int,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    body: GridLayoutManager.() -> Unit
) = GridLayoutManager(this, spanCount, orientation, reverseLayout).apply(body)

fun GridLayoutManager.setupSpanSizeLookup(getSpanSize: (position: Int) -> Int) = apply {
    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int) = getSpanSize(position)
    }
}