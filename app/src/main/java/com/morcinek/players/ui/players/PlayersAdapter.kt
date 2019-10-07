package com.morcinek.players.ui.players

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.morcinek.players.R
import com.morcinek.players.core.ViewHolder
import com.morcinek.players.core.data.PlayerData
import kotlinx.android.synthetic.main.vh_player.view.*

class PlayersAdapter : ListAdapter<PlayerData, ViewHolder>(object : DiffUtil.ItemCallback<PlayerData>() {

    override fun areItemsTheSame(oldItem: PlayerData, newItem: PlayerData) =
        oldItem.name + oldItem.surname + oldItem.birthDate == newItem.name + newItem.surname + newItem.birthDate

    override fun areContentsTheSame(oldItem: PlayerData, newItem: PlayerData) = true

}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.vh_player, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let {
            holder.itemView.apply {
                name.text = "${it.name} ${it.surname}"
            }
        }
    }

}