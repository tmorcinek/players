package com.morcinek.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.morcinek.recyclerview.ViewHolder
import kotlin.reflect.KClass

fun RecyclerView.sections(body: FSectionsAdapter.() -> Unit) {
    layoutManager = LinearLayoutManager(context)
    adapter = sectionsAdapter(body)
}

fun sectionsAdapter(body: FSectionsAdapter.() -> Unit) = FSectionsAdapter().apply(body)

class FSectionsAdapter : ListAdapter<HasKey, ViewHolder>(itemCallback()) {

    private val sectionViewAdapters = mutableMapOf<Int, FSection<Any>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = sectionViewAdapters[viewType]!!.onCreateViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        sectionViewAdapters[getItemViewType(position)]!!.onBindViewHolder(holder, getItem(position) , position)

    override fun getItemViewType(position: Int) = getItemViewTypeForClass(getItem(position).javaClass)

    private fun getItemViewTypeForClass(type: Class<*>) = type.hashCode()

    fun <T : HasKey> addSectionViewAdapter(type: KClass<T>, body: FSection<T>.() -> Unit) =
        sectionViewAdapters.put(getItemViewTypeForClass(type.java), FSection<T>().apply(body) as FSection<Any>)

    inline fun <reified T: HasKey> section(noinline body: FSection<T>.() -> Unit) = addSectionViewAdapter(T::class, body)

    inline fun <reified T: HasKey> section(resId: Int, noinline onBindView: (View.(position: Int, item: T) -> Unit)) = addSectionViewAdapter(T::class){
        resId(resId)
        onBindView(onBindView)
    }

    inline fun<reified T> itemAtPositionIsClass(position: Int) = getItemViewType(position) == T::class.java.hashCode()
}

class FSection<T> {

    private var resId: Int = 0
    private var onCreateView: ((ViewGroup) -> View) = { parent -> LayoutInflater.from(parent.context).inflate(resId, parent, false) }
    private var onBindView: (View.(Int, T) -> Unit) = { _, _ -> }

    fun resId(resId: Int) {
        this.resId = resId
    }

    fun onCreateView(onCreateView: (ViewGroup) -> View) {
        this.onCreateView = onCreateView
    }

    fun onBindView(onBindView: View.(position: Int, item: T) -> Unit) {
        this.onBindView = onBindView
    }

    internal fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(onCreateView.invoke(parent))

    internal fun onBindViewHolder(viewHolder: ViewHolder, item: T, position: Int) = viewHolder.itemView.onBindView(position, item)
}
