package com.morcinek.players.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import java.util.*

class PlayerViewModel : ViewModel() {

    private val _text = MutableLiveData<PlayerData>().apply {
        value = PlayerData("Tomasz", "Morcinek", Calendar.getInstance().apply { set(1988, 3, 21) }.time, null)
    }
    val text: LiveData<PlayerData> = _text
}