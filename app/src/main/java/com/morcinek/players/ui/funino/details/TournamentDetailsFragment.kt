package com.morcinek.players.ui.funino.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.ViewHolder
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.getParcelableOrNull
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.core.itemCallback
import com.morcinek.players.ui.funino.TournamentData
import kotlinx.android.synthetic.main.fragment_tournament_details.*
import kotlinx.android.synthetic.main.vh_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class TournamentDetailsFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_tournament_details

    private val viewModel by viewModelWithFragment<TournamentDetailsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            val parcelable = getParcelableOrNull<TournamentData>()
//            parcelable.finished
            viewModel.team.observe(this@TournamentDetailsFragment, Observer { details ->
                details.tournamentData.let {
                    title.text = it.title
                    subtitle.text = it.subtitle
                    isFinished.text = it.finished
                    isFinished.visibility = View.VISIBLE
                }
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.layoutAnimation = LayoutAnimationController(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in))
                recyclerView.adapter = PlayersAdapter().apply {
                    submitList(details.players)
                }
            })
        }
    }
}

private class PlayersAdapter : ListAdapter<PlayerData, ViewHolder>(itemCallback<PlayerData> {
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


val tournamentDetailsModule = module {
    viewModel { (fragment: Fragment) ->
        TournamentDetailsViewModel(fragment.getParcelable())
    }
}