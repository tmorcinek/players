package com.morcinek.players.ui.funino

import com.morcinek.players.core.data.PlayerData

class TournamentGameData(val gameId: Int, val homeTeamData: TeamData, val awayTeamData: TeamData, val scoreData: ScoreData?)

class TeamData(val color: Int, val players: Set<PlayerData>)
class ScoreData(val homeTeamScore: Int, val awayTeamScore: Int)
