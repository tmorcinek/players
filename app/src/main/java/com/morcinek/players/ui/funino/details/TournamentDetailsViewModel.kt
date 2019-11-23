package com.morcinek.players.ui.funino.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.getPlayers
import com.morcinek.players.core.database.valueEventListener
import com.morcinek.players.ui.funino.ScoreData
import com.morcinek.players.ui.funino.TeamData
import com.morcinek.players.ui.funino.TournamentData
import com.morcinek.players.ui.funino.TournamentGameData
import java.util.*

