package com.morcinek.players.core.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class PlayerTrainingData(var lateOnTraining: Boolean, var points: Int, var note: String) : HasToMap {
    override fun toMap(): Map<String, Any?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}