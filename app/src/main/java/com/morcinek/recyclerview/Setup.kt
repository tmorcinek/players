package com.morcinek.recyclerview

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setup(body: FSetup.() -> Unit) {
    FRecyclerViewMediator(this).apply(body)
}

interface FSetup {
    fun <T> list(diffCallback: DiffUtil.ItemCallback<T>, body: FListAdapter<T>.() -> Unit)
    fun adapter(adapter: RecyclerView.Adapter<*>)

    fun manager(layoutManager: RecyclerView.LayoutManager)
    fun linear(@RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL, reverseLayout: Boolean = false)
    fun horizontal(reverseLayout: Boolean = false) = linear(RecyclerView.HORIZONTAL, reverseLayout)
    fun grid(spanCount: Int,
             @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
             reverseLayout: Boolean = false,
             body: GridLayoutManager.() -> Unit = {}
    )
}

class FRecyclerViewMediator(private val recyclerView: RecyclerView) : FSetup {

    override fun <T> list(diffCallback: DiffUtil.ItemCallback<T>, body: FListAdapter<T>.() -> Unit) {
        recyclerView.adapter = FListAdapter(diffCallback).apply(body)
    }

    override fun adapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = adapter
    }

    override fun manager(layoutManager: RecyclerView.LayoutManager) {
        recyclerView.layoutManager = layoutManager
    }

    override fun linear(orientation: Int, reverseLayout: Boolean) {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context, orientation, reverseLayout)
    }

    override fun grid(spanCount: Int, orientation: Int, reverseLayout: Boolean, body: GridLayoutManager.() -> Unit) {
        recyclerView.layoutManager = GridLayoutManager(recyclerView.context, spanCount, orientation, reverseLayout).apply(body)
    }
}
