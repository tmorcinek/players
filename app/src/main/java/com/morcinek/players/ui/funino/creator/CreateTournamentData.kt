package com.morcinek.players.ui.funino.creator

import android.os.Parcelable
import com.morcinek.players.core.data.PlayerData
import kotlinx.android.parcel.Parcelize

@Parcelize
class CreateTournamentData(val players: Set<PlayerData>, val numberOfGames: Int = 0) : Parcelable {

    val numberOfPlayers: Int
        get() = players.size
}