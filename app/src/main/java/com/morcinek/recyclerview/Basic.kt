package com.morcinek.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

fun <T> staticAdapter(items: List<T>, body: FAdapter<T>.() -> Unit) = StaticAdapter(items).apply(body)

class StaticAdapter<T>(private val items: List<T>) : FAdapter<T>() {

    override fun getItem(position: Int) = items[position]

    override fun getItemCount() = items.size
}

abstract class FAdapter<T> : RecyclerView.Adapter<ViewHolder>() {

    private var resId: Int = 0
    private var onCreateView: ((ViewGroup, Int) -> View) =
        { parent, _ -> LayoutInflater.from(parent.context).inflate(resId, parent, false).apply(onViewCreated) }
    private var onViewCreated: (View.() -> Unit) = { }
    private var onBindView: (View.(Int, T) -> Unit) = { _, _ -> }
    private var onBindPayloads: (View.(Int, T, MutableList<Any>) -> Unit) = { _, _, _ -> }
    private var onDetached: (View.() -> Unit) = { }

    protected abstract fun getItem(position: Int): T

    fun resId(resId: Int, onViewCreated: View.() -> Unit = {}) {
        this.resId = resId
        this.onViewCreated = onViewCreated
    }

    fun onCreateView(onCreateView: (parent: ViewGroup, viewType: Int) -> View) {
        this.onCreateView = onCreateView
    }

    fun onBind(onBindView: View.(position: Int, item: T) -> Unit) {
        this.onBindView = onBindView
    }

    fun onBindPayloads(onBindPayloads: View.(position: Int, item: T, payloads: MutableList<Any>) -> Unit) {
        this.onBindPayloads = onBindPayloads
    }

    fun onDetached(onDetached: View.() -> Unit) {
        this.onDetached = onDetached
    }

    fun <V : View> onBindView(onBindView: V.(position: Int, item: T) -> Unit) {
        onBind(onBindView as View.(Int, T) -> Unit)
    }

    fun <V : View> onBindViewPayloads(onBindPayloads: V.(position: Int, item: T, payloads: MutableList<Any>) -> Unit) {
        onBindPayloads(onBindPayloads as View.(Int, T, MutableList<Any>) -> Unit)
    }

    fun <V : View> onViewDetached(onViewDetached: V.() -> Unit) {
        this.onDetached = onViewDetached as View.() -> Unit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(onCreateView(parent, viewType))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.itemView.onBindView(position, getItem(position))

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            holder.itemView.onBindPayloads(position, getItem(position), payloads)
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) = holder.itemView.onDetached()
}
