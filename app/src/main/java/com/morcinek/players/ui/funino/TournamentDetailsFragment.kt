package com.morcinek.players.ui.funino

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.clickableListAdapter
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.setDrawableColor
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.core.itemCallback
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_tournament_details.*
import kotlinx.android.synthetic.main.vh_game.view.*
import kotlinx.android.synthetic.main.vh_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class TournamentDetailsFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_tournament_details

    private val viewModel by viewModelWithFragment<TournamentDetailsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            viewModel.tournamentDetailsData.let { details ->
                details.tournamentData.let {
                    title.text = it.title
                    subtitle.text = it.subtitle
                    isFinished.text = it.finished
                    isFinished.visibility = View.VISIBLE
                }
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.adapter = clickableListAdapter(R.layout.vh_game, itemCallback()) { position, item: TournamentGameData, view ->
                    view.apply {
                        gameNumber.text = "Game ${position + 1}"
                        item.homeTeamData.let {
                            homeColor.setDrawableColor(it.color)
                            homeTeam.removeAllViews()
                            it.players.forEach { player ->
                                homeTeam.addView(TextView(context).apply { text = "${player.name} ${player.surname}" })
                            }
                        }
                        item.awayTeamData.let {
                            awayColor.setDrawableColor(it.color)
                            awayTeam.removeAllViews()
                            it.players.forEach { player ->
                                awayTeam.addView(TextView(context).apply { text = "${player.name} ${player.surname}" })
                            }
                        }
                        item.scoreData?.let {
                            homeScore.text = "${it.homeTeamScore}"
                            awayScore.text = "${it.awayTeamScore}"
                        }
                    }
                }.apply { submitList(details.tournamentGames) }
            }
        }
    }
}

private class TournamentDetailsViewModel(val tournamentDetailsData: TournamentDetailsData) : ViewModel()

val tournamentDetailsModule = module {
    viewModel { (fragment: Fragment) -> TournamentDetailsViewModel(fragment.getParcelable()) }
}

@Parcelize
class TournamentDetailsData(val tournamentData: TournamentData, val tournamentGames: List<TournamentGameData>) : Parcelable