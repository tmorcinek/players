package com.morcinek.players.ui.teams

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.getList
import com.morcinek.players.core.database.valueEventListener


class TeamsViewModel(private val references: FirebaseReferences) : ViewModel() {

    val teams: LiveData<List<TeamData>> = MutableLiveData<List<TeamData>>().apply {
        references.teamsReference().addListenerForSingleValueEvent(valueEventListener { postValue(it.getList<TeamData>().map { it.second }) })
    }
}
