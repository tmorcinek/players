package com.morcinek.core.ui

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListPopupWindow
import com.morcinek.players.R

fun <T> View.showPopupWindow(popupAdapter: PopupAdapter<T>, onCreate: ListPopupWindow.() -> Unit, onItemSelected: (T) -> Unit, onDismissed: () -> Unit = {}) =
    ListPopupWindow(context).apply {
        setBackgroundDrawable(resources.getDrawable(R.drawable.bg_popup_dark, null))
        setAdapter(popupAdapter)
        setOnItemClickListener { _, _, position, _ ->
            onItemSelected(popupAdapter.items[position])
            dismiss()
        }
        setOnDismissListener { onDismissed() }
        isModal = true
        anchorView = this@showPopupWindow
        onCreate()
    }.show()

class PopupAdapter<T>(val items: List<T>, val resId: Int, val onBind: View.(T) -> Unit) : BaseAdapter() {

    override fun getCount() = items.size

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
        (convertView ?: View.inflate(parent?.context, resId, null)).apply { onBind(getItem(position)) }
}