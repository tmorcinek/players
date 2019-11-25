package com.morcinek.players.core.data

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class TrainingData(var date: Date, var note: String, var players: List<Int>, var points: Map<Int,Int>) : HasToMap {
    override fun toMap(): Map<String, Any?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}