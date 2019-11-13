package com.morcinek.players.ui.funino.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.ui.funino.ScoreData
import com.morcinek.players.ui.funino.TeamData
import com.morcinek.players.ui.funino.TournamentData
import com.morcinek.players.ui.funino.TournamentGameData
import com.morcinek.players.ui.funino.creator.colors
import java.util.*

class TournamentDetailsViewModel(private val tournamentData: TournamentData) : ViewModel() {

    val team: LiveData<TournamentDetailsData> = MutableLiveData<TournamentDetailsData>().apply {
        val players = listOf(
            PlayerData("Tomasz", "Morcinek", Calendar.getInstance().apply { set(1988, 3, 21) }.time, null),
            PlayerData("Marek", "Piechniczek", Calendar.getInstance().apply { set(1988, 3, 21) }.time, null),
            PlayerData("Faustyn", "Marek", Calendar.getInstance().apply { set(1988, 3, 21) }.time, null),
            PlayerData("Guardian", "Zok", Calendar.getInstance().apply { set(1988, 3, 21) }.time, null),
            PlayerData("Dominik", "Czempik", Calendar.getInstance().apply { set(1988, 3, 21) }.time, null),
            PlayerData("Kamil", "Klusek", Calendar.getInstance().apply { set(1988, 3, 21) }.time, null)
        )
        value = TournamentDetailsData(
            tournamentData,
            players,
            listOf(
                TournamentGameData(1, TeamData(colors.first(), players.take(3).toSet()), TeamData(colors[1], players.takeLast(3).toSet()), null),
                TournamentGameData(2, TeamData(colors[2], players.take(3).toSet()), TeamData(colors[3], players.takeLast(3).toSet()), ScoreData(3,4))
            )
        )
    }
}