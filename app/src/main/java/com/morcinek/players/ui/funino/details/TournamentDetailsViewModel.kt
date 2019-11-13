package com.morcinek.players.ui.funino.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.ui.funino.TournamentData
import java.util.*

class TournamentDetailsViewModel(private val tournamentData: TournamentData) : ViewModel() {

    val team: LiveData<TournamentDetailsData> = MutableLiveData<TournamentDetailsData>().apply {
        value = TournamentDetailsData(
            tournamentData,
            listOf(
                PlayerData("Tomasz", "Morcinek", Calendar.getInstance().apply { set(1988, 3, 21) }.time, null),
                PlayerData("Marek", "Piechniczek", Calendar.getInstance().apply { set(1988, 3, 21) }.time, null),
                PlayerData("Faustyn", "Marek", Calendar.getInstance().apply { set(1988, 3, 21) }.time, null),
                PlayerData("Guardian", "Zok", Calendar.getInstance().apply { set(1988, 3, 21) }.time, null),
                PlayerData("Dominik", "Czempik", Calendar.getInstance().apply { set(1988, 3, 21) }.time, null)
            )
        )
    }
}