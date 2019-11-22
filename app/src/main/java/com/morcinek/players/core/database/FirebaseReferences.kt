package com.morcinek.players.core.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.morcinek.players.core.HasId
import com.morcinek.players.core.data.PlayerData

class FirebaseReferences(val auth: FirebaseAuth, val database: FirebaseDatabase) {

    private val userId: String
        get() = auth.currentUser!!.uid

    private fun userReference() = database.getReference("users").child(userId)

    fun playersReference() = userReference().child("players")

    fun teamsReference() = userReference().child("teams")
}

inline fun valueEventListener(crossinline function: (DataSnapshot) -> Unit) = object : ValueEventListener {

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        if (dataSnapshot.exists()) {
            function(dataSnapshot)
        }
    }

    override fun onCancelled(p0: DatabaseError) {}
}

inline fun <reified T> DataSnapshot.getList(): List<Pair<String, T>> = children.map { it.key!! to it.getValue(T::class.java)!! }

fun DataSnapshot.getPlayers(): List<PlayerData> = getList<PlayerData>().map { it.second.apply { id = it.first } }