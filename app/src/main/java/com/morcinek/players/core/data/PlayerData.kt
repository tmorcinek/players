package com.morcinek.players.core.data

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import com.morcinek.players.core.HasId
import kotlinx.android.parcel.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize
data class PlayerData(var name: String, var surname: String, var birthDate: Date, var photoUrl: String?) : FBData, Parcelable, HasId {
    override fun toMap(): Map<String, Any?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toString() = "$name $surname"

    override fun id() = name
}