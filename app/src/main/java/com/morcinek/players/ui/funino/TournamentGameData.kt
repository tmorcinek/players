package com.morcinek.players.ui.funino

import android.os.Parcelable
import com.morcinek.players.core.HasId
import com.morcinek.players.core.data.PlayerData
import kotlinx.android.parcel.Parcelize

@Parcelize
class TournamentGameData(val gameId: Int, val homeTeamData: TeamData, val awayTeamData: TeamData, val scoreData: ScoreData?) : HasId, Parcelable {
    override fun id() = gameId.toString()
}

@Parcelize
class TeamData(val color: Int, val players: Set<PlayerData>) : Parcelable

@Parcelize
class ScoreData(val homeTeamScore: Int, val awayTeamScore: Int) : Parcelable
