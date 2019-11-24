package com.morcinek.players.core.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData

fun FirebaseReferences.playersLiveDataForValueListener(): LiveData<List<PlayerData>> = MutableLiveData<List<PlayerData>>().apply {
    playersReference().addValueEventListener(valueEventListener { postValue(it.getPlayers()) })
}

fun FirebaseReferences.teamsLiveDataForValueListener(): LiveData<List<TeamData>> = MutableLiveData<List<TeamData>>().apply {
    teamsReference().addListenerForSingleValueEvent(valueEventListener { postValue(it.getList<TeamData>().map { it.second }) })
}