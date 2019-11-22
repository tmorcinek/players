package com.morcinek.players.ui.funino

import com.morcinek.players.core.HasId
import com.morcinek.players.core.data.PlayerData

class TournamentGameData(val gameId: Int, val homeTeamData: TeamData, val awayTeamData: TeamData, val scoreData: ScoreData?) : HasId {
    override fun id() = gameId.toString()
}

class TeamData(val color: Int, val players: Set<PlayerData>)
class ScoreData(val homeTeamScore: Int, val awayTeamScore: Int)
