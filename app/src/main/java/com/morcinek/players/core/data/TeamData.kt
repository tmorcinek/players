package com.morcinek.players.core.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.morcinek.players.core.HasKey

@IgnoreExtraProperties
data class TeamData(
    @Exclude override var key: String = "",
    var name: String = ""
//    var category: String,
//    var year: Int
) : HasKey, HasToMap {
    override fun toMap() = mapOf( "name" to name)
}