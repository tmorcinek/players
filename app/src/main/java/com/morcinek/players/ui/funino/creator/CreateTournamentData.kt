package com.morcinek.players.ui.funino.creator

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CreateTournamentData(val numberOfPlayers: Int, val numberOfGames: Int = 0) : Parcelable