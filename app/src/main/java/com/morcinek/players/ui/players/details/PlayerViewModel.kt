package com.morcinek.players.ui.players.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morcinek.players.core.data.PlayerData
import java.util.*

class PlayerViewModel : ViewModel() {

    val text: LiveData<PlayerData> = MutableLiveData<PlayerData>().apply {
        value = PlayerData("key", "Tomasz", "Morcinek", Calendar.getInstance().apply { set(1988, 3, 21) }.timeInMillis, null)
    }
}