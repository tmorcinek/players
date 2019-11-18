package com.morcinek.players.core.data

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import java.time.Year
import java.util.*

@IgnoreExtraProperties
@Parcelize
data class PlayerData(var name: String, var surname: String, var birthDate: Date, var photoUrl: String?) : FBData, Parcelable {
    override fun toMap(): Map<String, Any?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toString() = "$name $surname"

    override fun hashCode() = toString().hashCode()

    override fun equals(other: Any?) = other is PlayerData && other.hashCode() == hashCode()
}