package com.morcinek.players.core.data

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.morcinek.players.core.HasKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize
data class PlayerData(
    @Exclude override var key: String = "",
    var name: String = "",
    var surname: String = "",
    var birthDateInMillis: Long = 0,
    var photoUrl: String? = null
) : Parcelable, HasKey {

    @Exclude
    fun getBirthDate() = Calendar.getInstance().apply { timeInMillis = birthDateInMillis }

    @Exclude
    fun setBirthDate(calendar: Calendar) {
        birthDateInMillis = calendar.timeInMillis
    }

    override fun toString() = "$name $surname"
}