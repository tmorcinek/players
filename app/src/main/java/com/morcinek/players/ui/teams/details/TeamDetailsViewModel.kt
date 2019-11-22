package com.morcinek.players.ui.teams.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morcinek.players.core.data.TeamData

class TeamDetailsViewModel : ViewModel() {

    val team: LiveData<TeamData> = MutableLiveData<TeamData>().apply {
        value = TeamData("Skrzaty 2019", "Skrzaty", 2019)
    }
}