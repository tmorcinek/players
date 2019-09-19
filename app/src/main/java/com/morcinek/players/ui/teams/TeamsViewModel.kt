package com.morcinek.players.ui.teams

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morcinek.players.core.data.TeamData

class TeamsViewModel : ViewModel() {

    private val _text = MutableLiveData<TeamData>().apply {
        value = TeamData("Skrzaty 2001", "Skrzaty", 2019, listOf())
    }
    val text: LiveData<TeamData> = _text
}