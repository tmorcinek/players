package com.morcinek.players.ui.funino

import android.os.Parcelable
import com.morcinek.players.core.HasKey
import com.morcinek.players.core.data.PlayerData
import kotlinx.android.parcel.Parcelize

@Parcelize
class TournamentData(val id: Long, val title: String, val subtitle: String, val finished: String, val isToday: Boolean) : Parcelable

@Parcelize
class TournamentGameData(val gameId: Int, val homeTeamData: TeamData, val awayTeamData: TeamData, val scoreData: ScoreData?) : HasKey, Parcelable {
    override var key = gameId.toString()
}

@Parcelize
class TeamData(val color: Int, val players: Set<PlayerData>) : Parcelable

@Parcelize
class ScoreData(val homeTeamScore: Int, val awayTeamScore: Int) : Parcelable
