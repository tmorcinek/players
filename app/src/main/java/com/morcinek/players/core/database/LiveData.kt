package com.morcinek.players.core.database

import androidx.lifecycle.MutableLiveData
import com.morcinek.players.core.data.PlayerData

fun FirebaseReferences.playersLiveDataForValueListener() = MutableLiveData<List<PlayerData>>().apply {
    playersReference().addValueEventListener(valueEventListener { postValue(it.getPlayers()) })
}