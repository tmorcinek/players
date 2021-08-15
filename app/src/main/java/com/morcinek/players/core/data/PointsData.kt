package com.morcinek.players.core.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PointsData(
    var name: String = "",
    var id: Int = 0,
    var playersPoints: Map<String, Int> = emptyMap()
) : Parcelable