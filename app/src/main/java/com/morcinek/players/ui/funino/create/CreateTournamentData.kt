package com.morcinek.players.ui.funino.create

import android.os.Parcelable
import com.morcinek.players.core.data.PlayerData
import kotlinx.android.parcel.Parcelize

@Parcelize
class CreateTournamentData(val players: Set<PlayerData>, var games: List<Pair<Set<Int>, Set<Int>>> = listOf(), var colors: Set<Color> = setOf()) : Parcelable {

    val numberOfPlayers: Int
        get() = players.size
}