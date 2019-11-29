package com.morcinek.players.core.data

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.morcinek.players.core.HasKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class TeamData(
    @get:Exclude override var key: String = "",
    var name: String = ""
) : HasKey, HasToMap, Parcelable {
    override fun toMap() = mapOf("name" to name)

    override fun toString() = name
}