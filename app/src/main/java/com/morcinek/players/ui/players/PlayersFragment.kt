package com.morcinek.players.ui.players

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.ViewHolder
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.itemCallback
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.vh_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class PlayersFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_list

    private val viewModel by viewModel<PlayersViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.layoutAnimation = LayoutAnimationController(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in))
            recyclerView.adapter = PlayersAdapter().apply {
                viewModel.players.observe(this@PlayersFragment, Observer { submitList(it) })
            }
        }
    }
}

class PlayersAdapter : ListAdapter<PlayerData, ViewHolder>(itemCallback<PlayerData> {
    areItemsTheSame { oldItem, newItem ->
        oldItem.name + oldItem.surname + oldItem.birthDate == newItem.name + newItem.surname + newItem.birthDate
    }
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

val playersModule = module {
    viewModel { PlayersViewModel() }
}