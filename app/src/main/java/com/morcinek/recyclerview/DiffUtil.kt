package com.morcinek.recyclerview

import androidx.recyclerview.widget.DiffUtil


fun <T> itemCallback(function: ItemCallback<T>.() -> Unit) = ItemCallback<T>().apply(function)

fun <T : HasKey> itemCallback() = itemCallback<T> {
    areItemsTheSame { t, t2 -> t.key == t2.key }
}

interface HasKey {
    val key: String
}

class ItemCallback<T> : DiffUtil.ItemCallback<T>() {

    private var _areItemsTheSame: (T, T) -> Boolean = { _, _ -> throw NotImplementedError() }
    private var _areContentsTheSame: (T, T) -> Boolean = { t1, t2 -> t1 == t2 }

    fun areItemsTheSame(function: (T, T) -> Boolean) {
        _areItemsTheSame = function
    }

    fun areContentsTheSame(function: (T, T) -> Boolean) {
        _areContentsTheSame = function
    }

    override fun areItemsTheSame(oldItem: T, newItem: T) = _areItemsTheSame(oldItem, newItem)

    override fun areContentsTheSame(oldItem: T, newItem: T) = _areContentsTheSame(oldItem, newItem)
}
