package com.morcinek.players.core.data

import android.graphics.Color
import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.morcinek.players.core.extensions.toStandardString
import kotlinx.parcelize.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize
data class EventData(
    @get:Exclude override var key: String = "",
    var dateInMillis: Long = 0,
    var type: Type? = null,
    var optional: Boolean = false,
    var players: List<String> = listOf(),
    var points: List<PointsData> = listOf()
) : Parcelable, FirebaseKey {

    @Exclude
    fun getDate() = Calendar.getInstance().apply { timeInMillis = dateInMillis }

    @Exclude
    fun setDate(calendar: Calendar) {
        dateInMillis = calendar.timeInMillis
    }

    override fun toString() = "$type on ${getDate().toStandardString()}"

    fun playerPointsSum(playerKey: String) = points.sumOf { it.playersPoints[playerKey] ?: 0 }

    enum class Type { Training, Game, Tournament, Friendly }
}

fun eventTypeColor(type: EventData.Type?) = when (type) {
    EventData.Type.Training -> Color.parseColor("#51A557")
    EventData.Type.Friendly -> Color.parseColor("#EDA8C7")
    EventData.Type.Game -> Color.parseColor("#FF696A")
    EventData.Type.Tournament -> Color.parseColor("#E8C7A6")
    null -> Color.parseColor("#FFFFFFFF")
}
