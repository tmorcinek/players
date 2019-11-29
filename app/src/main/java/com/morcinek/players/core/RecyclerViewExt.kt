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

fun <T : HasKey> itemCallback() = ItemCallback<T>().apply {
    areItemsTheSame { t, t2 -> t.key == t2.key }
    areContentsTheSame { t, t2 -> t == t2 }
}

interface HasKey {
    var key: String
}

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

abstract class SimpleListAdapter<T>(private val vhResourceId: Int, diffCallback: ItemCallback<T>) : ListAdapter<T, ViewHolder>(diffCallback) {

    protected abstract fun onBindViewHolder(item: T, view: View)

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(vhResourceId, parent, false))

    final override fun onBindViewHolder(holder: ViewHolder, position: Int) = onBindViewHolder(getItem(position), holder.itemView)
}

abstract class ClickableListAdapter<T>(vhResourceId: Int, diffCallback: ItemCallback<T>) : SimpleListAdapter<T>(vhResourceId, diffCallback) {

    private var _onClickListener: ((View, T) -> Unit) = { _, _ -> }

    fun onClickListener(function: (View, T) -> Unit) {
        _onClickListener = function
    }

    fun onItemClickListener(function: (T) -> Unit) {
        _onClickListener = { _, item -> function(item)}
    }

    @CallSuper
    override fun onBindViewHolder(item: T, view: View) {
        view.setOnClickListener { _onClickListener(it, item) }
    }
}

abstract class SelectableListAdapter<T>(vhResourceId: Int, diffCallback: ItemCallback<T>) : ClickableListAdapter<T>(vhResourceId, diffCallback) {

    var selectedItems: Set<T> = setOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    @CallSuper
    override fun onBindViewHolder(item: T, view: View) {
        super.onBindViewHolder(item, view)
        view.isSelected = item in selectedItems
    }
}

fun <T> simpleListAdapter(vhResourceId: Int, diffCallback: ItemCallback<T>, onBindView: (item: T, view: View) -> Unit) = object : SimpleListAdapter<T>(vhResourceId, diffCallback) {
    override fun onBindViewHolder(item: T, view: View) = onBindView(item, view)
}

fun <T> clickableListAdapter(vhResourceId: Int, diffCallback: ItemCallback<T>, onBindView: (item: T, view: View) -> Unit) = object : ClickableListAdapter<T>(vhResourceId, diffCallback) {

    override fun onBindViewHolder(item: T, view: View) {
        super.onBindViewHolder(item, view)
        onBindView(item, view)
    }
}

fun <T> selectableListAdapter(vhResourceId: Int, diffCallback: ItemCallback<T>, onBindView: (item: T, view: View) -> Unit) = object : SelectableListAdapter<T>(vhResourceId, diffCallback) {

    override fun onBindViewHolder(item: T, view: View) {
        super.onBindViewHolder(item, view)
        onBindView(item, view)
    }
}

