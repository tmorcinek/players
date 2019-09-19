package com.morcinek.players.core.data

import com.google.firebase.database.IgnoreExtraProperties
import java.time.Year

@IgnoreExtraProperties
class TeamData(var name: String, var category: String, var year: Int, var players: List<Int>) : FBData {
    override fun toMap(): Map<String, Any?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}