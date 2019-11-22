package com.morcinek.players.core.data

import com.google.firebase.database.IgnoreExtraProperties
import com.morcinek.players.core.HasId

@IgnoreExtraProperties
data class TeamData(var name: String, var category: String, var year: Int) : FBData, HasId {
    override fun toMap(): Map<String, Any?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun id() = name

//    override fun equals(other: Any?) = other is TeamData && other.name == name && other.category
}