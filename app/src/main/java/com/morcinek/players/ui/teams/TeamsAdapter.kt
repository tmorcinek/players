package com.morcinek.players.ui.teams

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.morcinek.players.R
import com.morcinek.players.core.ViewHolder
import com.morcinek.players.core.data.TeamData
import kotlinx.android.synthetic.main.vh_player.view.*

private val itemCallback = object : DiffUtil.ItemCallback<TeamData>() {

    override fun areItemsTheSame(oldItem: TeamData, newItem: TeamData) =
        oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: TeamData, newItem: TeamData) = true

}

class TeamsAdapter : ListAdapter<TeamData, ViewHolder>(itemCallback({ oldItem, newItem -> oldItem.name == newItem.name })) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.vh_player, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let {
            holder.itemView.apply {
                name.text = "${it.name}/${it.category}"
            }
        }
    }

}


private inline fun <T> itemCallback(crossinline areItemsTheSame: (T, T) -> Boolean, crossinline areContentsTheSame: (T, T) -> Boolean = { _, _ -> true }) =
    object : DiffUtil.ItemCallback<T>() {

        override fun areItemsTheSame(oldItem: T, newItem: T) = areItemsTheSame(oldItem, newItem)

        override fun areContentsTheSame(oldItem: T, newItem: T) = areContentsTheSame(oldItem, newItem)
    }