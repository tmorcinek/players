package com.morcinek.players.ui.funino.details

import android.os.Parcelable
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.ui.funino.TournamentData
import com.morcinek.players.ui.funino.TournamentGameData
import kotlinx.android.parcel.Parcelize

@Parcelize
class TournamentDetailsData(val tournamentData: TournamentData, val tournamentGames: List<TournamentGameData>) : Parcelable