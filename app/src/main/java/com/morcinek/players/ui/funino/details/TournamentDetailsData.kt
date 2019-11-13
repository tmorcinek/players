package com.morcinek.players.ui.funino.details

import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.ui.funino.TournamentData
import com.morcinek.players.ui.funino.TournamentGameData

class TournamentDetailsData(val tournamentData: TournamentData, val players: List<PlayerData>, val tournamentGames: List<TournamentGameData>)