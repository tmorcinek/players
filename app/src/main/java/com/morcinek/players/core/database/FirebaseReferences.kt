package com.morcinek.players.core.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.data.FirebaseKey
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.extensions.dayOfMonth
import com.morcinek.players.core.extensions.getValue
import com.morcinek.players.core.extensions.month
import com.morcinek.players.core.extensions.year
import java.util.Calendar

class FirebaseReferences(private val auth: FirebaseAuth, val database: FirebaseDatabase) {

    private val userId: String
        get() = auth.currentUser!!.uid

    fun rootReference() = database.getReference("users").child(userId)

    fun playersReference() = rootReference().child("players")

    fun teamsReference() = rootReference().child("teams")

    fun teamEventsReference(teamKey: String) = rootReference().child("teams").child(teamKey).child("events")

    fun teamEventsReferenceFromAugust(teamKey: String) = teamEventsReference(teamKey)
        .orderByChild("dateInMillis")
        .startAt(Calendar.getInstance().apply {
            year = 2021
            month = 6
            dayOfMonth = 1
        }.timeInMillis.toDouble())

    fun teamEventReference(teamKey: String, eventKey: String) = rootReference().child("teams").child(teamKey).child("events").child(eventKey)
}

inline fun valueEventListener(crossinline function: (DataSnapshot) -> Unit) = object : ValueEventListener {

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        function(dataSnapshot)
    }

    override fun onCancelled(p0: DatabaseError) {}
}

inline fun <reified T> DataSnapshot.getList(): List<Pair<String, T>> = children.map { it.key!! to it.getValue<T>()!! }
inline fun <reified T : FirebaseKey> DataSnapshot.getHasKeyObjects(): List<T> = getList<T>().map { it.second.apply { key = it.first } }

inline fun <reified T : FirebaseKey> Query.objectsLiveDataForValueListener(): LiveData<List<T>> = MutableLiveData<List<T>>().apply {
    addValueEventListener(valueEventListener { postValue(it.getHasKeyObjects()) })
}
inline fun <reified T : FirebaseKey> Query.objectLiveDataForValueListener(): LiveData<T?> = MutableLiveData<T?>().apply {
    addValueEventListener(valueEventListener { postValue(it.getValue<T?>()?.apply { key = it.key!! }) })
}

fun FirebaseReferences.playersWithoutTeamLiveDataForValueListener(): LiveData<List<PlayerData>> = playersReference().orderByChild("teamKey").equalTo(null).objectsLiveDataForValueListener()
fun FirebaseReferences.playersForTeamLiveDataForValueListener(teamKey: String): LiveData<List<PlayerData>> = playersReference().orderByChild("teamKey").equalTo(teamKey).objectsLiveDataForValueListener()

fun FirebaseReferences.teamsLiveDataForValueListener(): LiveData<List<TeamData>> = teamsReference().objectsLiveDataForValueListener()

fun FirebaseReferences.eventsForTeamLiveDataForValueListener(teamKey: String, numberOfRecords: Int? = null): LiveData<List<EventData>> =
    teamEventsReferenceFromAugust(teamKey).let { if (numberOfRecords != null) it.limitToLast(numberOfRecords) else it }.objectsLiveDataForValueListener()

fun FirebaseReferences.eventForTeamLiveDataForValueListener(teamKey: String, eventKey: String): LiveData<EventData?> = teamEventReference(teamKey, eventKey).objectLiveDataForValueListener()
