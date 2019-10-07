package com.morcinek.players.ui.teams

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morcinek.players.core.data.TeamData

class TeamsViewModel : ViewModel() {

    val teams: LiveData<List<TeamData>> = MutableLiveData<List<TeamData>>().apply {
        value = listOf(
            TeamData("Skrzaty 2019", "Skrzaty", 2019, listOf()),
            TeamData("Zaki 2019", "Zaki", 2019, listOf())
        )
    }
}