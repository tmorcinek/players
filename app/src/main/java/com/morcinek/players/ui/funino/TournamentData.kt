package com.morcinek.players.ui.funino

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TournamentData(val id: Long, val title: String, val subtitle: String, val finished: String, val isToday: Boolean) : Parcelable