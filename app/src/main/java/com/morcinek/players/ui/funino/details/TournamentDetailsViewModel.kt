package com.morcinek.players.ui.funino.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.ui.funino.ScoreData
import com.morcinek.players.ui.funino.TeamData
import com.morcinek.players.ui.funino.TournamentData
import com.morcinek.players.ui.funino.TournamentGameData
import java.util.*

class TournamentDetailsViewModel(private val tournamentData: TournamentData) : ViewModel() {

    val team: LiveData<TournamentDetailsData> = MutableLiveData<TournamentDetailsData>().apply {
        val players = listOf(
            PlayerData("1","Tomasz", "Morcinek", Calendar.getInstance().apply { set(1988, 3, 21) }.timeInMillis, null),
            PlayerData("2","Marek", "Piechniczek", Calendar.getInstance().apply { set(1988, 3, 21) }.timeInMillis, null),
            PlayerData("2","Faustyn", "Marek", Calendar.getInstance().apply { set(1988, 3, 21) }.timeInMillis, null),
            PlayerData("2","Guardian", "Zok", Calendar.getInstance().apply { set(1988, 3, 21) }.timeInMillis, null),
            PlayerData("2","Dominik", "Czempik", Calendar.getInstance().apply { set(1988, 3, 21) }.timeInMillis, null),
            PlayerData("2","Kamil", "Klusek", Calendar.getInstance().apply { set(1988, 3, 21) }.timeInMillis, null)
        )
        value = TournamentDetailsData(
            tournamentData,
            players,
            listOf(
                TournamentGameData(1, TeamData(0xFFA93226.toInt(), players.take(3).toSet()), TeamData(0xFF633974.toInt(), players.takeLast(3).toSet()), null),
                TournamentGameData(2, TeamData(0xFF1A5276.toInt(), players.take(3).toSet()), TeamData(0xFF196F3D.toInt(), players.takeLast(3).toSet()), ScoreData(3,4))
            )
        )
    }
}