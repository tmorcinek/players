package com.morcinek.players.core.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.morcinek.players.core.HasKey
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData

class FirebaseReferences(private val auth: FirebaseAuth, val database: FirebaseDatabase) {

    private val userId: String
        get() = auth.currentUser!!.uid

    fun rootReference() = database.getReference("users").child(userId)

    fun playersReference() = rootReference().child("players")

    fun teamsReference() = rootReference().child("teams")
}

inline fun valueEventListener(crossinline function: (DataSnapshot) -> Unit) = object : ValueEventListener {

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        function(dataSnapshot)
    }

    override fun onCancelled(p0: DatabaseError) {}
}

inline fun <reified T> DataSnapshot.getList(): List<Pair<String, T>> = children.map { it.key!! to it.getValue(T::class.java)!! }

inline fun <reified T : HasKey> DataSnapshot.getHasKeyObjects(): List<T> = getList<T>().map { it.second.apply { key = it.first } }

inline fun <reified T : HasKey> Query.objectsLiveDataForValueListener(): LiveData<List<T>> = MutableLiveData<List<T>>().apply {
    addValueEventListener(valueEventListener { postValue(it.getHasKeyObjects()) })
}

fun FirebaseReferences.playersLiveDataForValueListener(): LiveData<List<PlayerData>> = playersReference().objectsLiveDataForValueListener()
fun FirebaseReferences.playersWithoutTeamLiveDataForValueListener(): LiveData<List<PlayerData>> = playersReference().orderByChild("teamKey").equalTo(null).objectsLiveDataForValueListener()
fun FirebaseReferences.playersForTeamLiveDataForValueListener(teamKey: String): LiveData<List<PlayerData>> = playersReference().orderByChild("teamKey").equalTo(teamKey).objectsLiveDataForValueListener()

fun FirebaseReferences.teamsLiveDataForValueListener(): LiveData<List<TeamData>> = teamsReference().objectsLiveDataForValueListener()

