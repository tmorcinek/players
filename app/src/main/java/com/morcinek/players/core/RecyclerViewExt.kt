package com.morcinek.players.core

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.morcinek.players.core.database.valueLiveData

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

    protected abstract fun onBindViewHolder(position: Int, item: T, view: View)

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(vhResourceId, parent, false))

    final override fun onBindViewHolder(holder: ViewHolder, position: Int) = onBindViewHolder(position, getItem(position), holder.itemView)
}

abstract class ClickableListAdapter<T>(vhResourceId: Int, diffCallback: ItemCallback<T>) : SimpleListAdapter<T>(vhResourceId, diffCallback) {

    private var _onClickListener: ((View, T) -> Unit) = { _, _ -> }

    fun onClickListener(function: (View, T) -> Unit) {
        _onClickListener = function
    }

    fun onItemClickListener(function: (T) -> Unit) {
        _onClickListener = { _, item -> function(item) }
    }

    @CallSuper
    override fun onBindViewHolder(position: Int, item: T, view: View) {
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
    override fun onBindViewHolder(position: Int, item: T, view: View) {
        super.onBindViewHolder(position, item, view)
        view.isSelected = item in selectedItems
    }
}

inline fun <T> listAdapter(vhResourceId: Int, diffCallback: ItemCallback<T>, crossinline onBindView: View.(position: Int, item: T) -> Unit) =
    object : ListAdapter<T, ViewHolder>(diffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(vhResourceId, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.itemView.onBindView(position, getItem(position))
    }

class SelectListAdapter<T>(private val vhResourceId: Int, diffCallback: ItemCallback<T>, private val onBindView: View.(position: Int, item: T) -> Unit) :
    ListAdapter<T, ViewHolder>(diffCallback) {

    val selectedItems = valueLiveData(setOf<T>())

    private val mutableSelectedItems = (selectedItems as MutableLiveData)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(vhResourceId, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.itemView.run {
        getItem(position).let { item ->
            setOnClickListener { select(item) }
            onBindView(position, item)
            isSelected = item in selectedItems.value!!
        }
    }

    override fun submitList(list: List<T>?) {
        mutableSelectedItems.postValue(list?.intersect(selectedItems.value!!) ?: setOf())
        super.submitList(list)
    }

    private fun select(item: T){
        mutableSelectedItems.postValue(selectedItems.updateSelectedItem(item))
        notifyDataSetChanged()
    }

    private fun <T> LiveData<Set<T>>.updateSelectedItem(item: T) = value!!.let { selectedItems ->
        if (item in selectedItems) selectedItems.minus(item) else selectedItems.plus(item)
    }
}
