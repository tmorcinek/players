package com.morcinek.players.core.ui

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.widget.PopupWindowCompat

class ListDropDownBuilder(private val context: Context, width: Int = ViewGroup.LayoutParams.MATCH_PARENT, height: Int = ViewGroup.LayoutParams.WRAP_CONTENT) {

    private var window: PopupWindow = PopupWindow(ListView(context), width, height, true)

    fun setBackgroundDrawable(@DrawableRes drawableRes: Int): ListDropDownBuilder = also {
        window.setBackgroundDrawable(context.resources.getDrawable(drawableRes))
    }

    fun setOnDismissListener(onDismiss: () -> Unit): ListDropDownBuilder = also {
        window.setOnDismissListener { onDismiss() }
    }

    fun <T> setAdapter(@LayoutRes itemLayoutRes: Int, items: List<T>, onItemSelected: (T) -> Unit): ListDropDownBuilder = also {
        (window.contentView as ListView).apply {
            adapter = ArrayAdapter(context, itemLayoutRes, android.R.id.text1, items)
            setSelector(android.R.color.transparent)
            dividerHeight = 0
            setOnItemClickListener { parent, _, position, _ ->
                onItemSelected(parent.getItemAtPosition(position) as T)
                window.dismiss()
            }
        }
    }

    fun showAsDropDown(anchor: View) {
        PopupWindowCompat.showAsDropDown(window, anchor, 0, 0, Gravity.CENTER_HORIZONTAL)
    }
}

fun showDropDown(
    view: View,
    width: Int = view.width,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    function: ListDropDownBuilder.() -> Unit) {
    ListDropDownBuilder(view.context, width, height).apply {
        function(this)
        showAsDropDown(view)
    }
}
