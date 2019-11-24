package com.morcinek.players.core.data

import com.google.firebase.database.IgnoreExtraProperties
import com.morcinek.players.core.HasKey

@IgnoreExtraProperties
data class TeamData(
    override var id: String,
    var name: String,
    var category: String,
    var year: Int
) : HasKey