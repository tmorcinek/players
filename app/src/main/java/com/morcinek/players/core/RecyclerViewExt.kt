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

inline fun <T> listAdapter(vhResourceId: Int, diffCallback: ItemCallback<T>, crossinline onBindView: View.(position: Int, item: T) -> Unit) =
    object : ListAdapter<T, ViewHolder>(diffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(vhResourceId, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.itemView.onBindView(position, getItem(position))
    }

class SelectionListAdapter<T>(private val vhResourceId: Int, diffCallback: ItemCallback<T>, private val selectionMode: SelectionMode = MultiSelect(), private val onBindView: View.(position: Int, item: T) -> Unit) :
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
        if (item in selectedItems) {
            selectedItems.minus(item)
        } else when(selectionMode){
            is SingleSelect -> setOf(item)
            is MultiSelect -> when  {
                selectionMode.limit == null-> selectedItems.plus(item)
                selectionMode.limit > selectedItems.size -> selectedItems.plus(item)
                else -> selectedItems
            }
        }
    }
}

sealed class SelectionMode
object SingleSelect : SelectionMode()
data class MultiSelect(val limit: Int? = null) : SelectionMode()