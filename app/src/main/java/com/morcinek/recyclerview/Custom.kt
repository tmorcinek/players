package com.morcinek.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.morcinek.recyclerview.ViewHolder

fun customAdapter(body: CAdapter.() -> Unit) = CAdapter().apply(body)

class CAdapter : RecyclerView.Adapter<ViewHolder>() {

    private val itemsTypes = mutableListOf<CItem>()

    fun item(body: CItem.() -> Unit) {
        itemsTypes.add(CItem(itemCount).apply(body))
    }

    fun item(resId: Int, onBind: View.() -> Unit) {
        itemsTypes.add(CItem(itemCount).apply {
            resId(resId)
            onBind(onBind)
        })
    }
    fun <T: View> itemView(resId: Int, onBindView: T.() -> Unit) {
        itemsTypes.add(CItem(itemCount).apply {
            resId(resId)
            onBindView(onBindView)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = itemsTypes[viewType].onCreateViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = itemsTypes[position].onBindViewHolder(holder)

    override fun getItemViewType(position: Int) = position

    override fun getItemCount() = itemsTypes.size
}

class CItem(val position: Int) {

    private var resId: Int = 0
    private var onCreateView: ((ViewGroup) -> View) = { parent -> LayoutInflater.from(parent.context).inflate(resId, parent, false) }
    private var onBind: (View.() -> Unit) = { }

    fun resId(resId: Int) {
        this.resId = resId
    }

    fun onBind(onBind: View.() -> Unit) {
        this.onBind = onBind
    }

    fun <T: View> onBindView(onBindView: T.() -> Unit) {
        this.onBind = onBindView as View.() -> Unit
    }

    internal fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(onCreateView.invoke(parent))

    internal fun onBindViewHolder(viewHolder: ViewHolder) = viewHolder.itemView.onBind()
}