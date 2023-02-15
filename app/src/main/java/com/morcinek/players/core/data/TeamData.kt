package com.morcinek.players.core.data

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.morcinek.android.HasKey
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
data class TeamData(
    @get:Exclude override var key: String = "",
    var name: String = "",
    var hiddenPlayers: List<String> = listOf()
) : FirebaseKey, HasToMap, Parcelable {
    override fun toMap() = mapOf("name" to name)

    override fun toString() = name
}