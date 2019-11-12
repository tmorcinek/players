package com.morcinek.players.ui.funino

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TournamentsViewModel : ViewModel() {

    val players: LiveData<List<TournamentData>> = MutableLiveData<List<TournamentData>>().apply {
        value = listOf(
            TournamentData(1, "Tuesday 12 Listopada", "12 players", "Not Finished", true),
            TournamentData(2, "Tuesday 11 Listopada", "8 players", "Finished", false)
        )
    }
}