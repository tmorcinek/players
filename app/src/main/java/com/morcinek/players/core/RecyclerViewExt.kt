package com.morcinek.players.core

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

fun <T> itemCallback(function: ItemCallback<T>.() -> Unit) = ItemCallback<T>().apply(function)

class ItemCallback<T> : DiffUtil.ItemCallback<T>() {

    private var _areItemsTheSame: (T, T) -> Boolean = { _, _ -> true }
    private var _areContentsTheSame: (T, T) -> Boolean = { _, _ -> true }

    fun areItemsTheSame(function: (T, T) -> Boolean) {
        _areItemsTheSame = function
    }

    fun areContentsTheSame(function: (T, T) -> Boolean) {
        _areContentsTheSame = function
    }

    override fun areItemsTheSame(oldItem: T, newItem: T) = _areItemsTheSame(oldItem, newItem)

    override fun areContentsTheSame(oldItem: T, newItem: T) = _areContentsTheSame(oldItem, newItem)
}

abstract class SimpleListAdapter<T>(diffCallback: ItemCallback<T>) : ListAdapter<T, ViewHolder>(diffCallback) {

    protected abstract val vhResourceId: Int
    protected abstract fun onBindViewHolder(item: T, view: View)


    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(vhResourceId, parent, false))

    final override fun onBindViewHolder(holder: ViewHolder, position: Int) = onBindViewHolder(getItem(position), holder.itemView)
}

abstract class ClickableListAdapter<T>(diffCallback: ItemCallback<T>) : SimpleListAdapter<T>(diffCallback) {

    private var _onClickListener: ((View, T) -> Unit) = { _, _ -> }

    fun onClickListener(function: (View, T) -> Unit) {
        _onClickListener = function
    }

    @CallSuper
    override fun onBindViewHolder(item: T, view: View) {
        view.setOnClickListener { _onClickListener(it, item) }
    }
}