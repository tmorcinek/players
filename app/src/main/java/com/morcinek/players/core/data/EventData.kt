package com.morcinek.players.core.data

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.morcinek.players.core.HasKey
import com.morcinek.players.core.extensions.toStandardString
import kotlinx.android.parcel.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize
data class EventData(
    @get:Exclude override var key: String = "",
    var dateInMillis: Long = 0,
    var type: String = "",
    var optional: Boolean = false,
    var players: List<String> = listOf(),
    var points: List<PointsData> = listOf()
) : Parcelable, HasKey {

    @Exclude
    fun getDate() = Calendar.getInstance().apply { timeInMillis = dateInMillis }

    @Exclude
    fun setDate(calendar: Calendar) {
        dateInMillis = calendar.timeInMillis
    }

    override fun toString() = "$type on ${getDate().toStandardString()}"
}