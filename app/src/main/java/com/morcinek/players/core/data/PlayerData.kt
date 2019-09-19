package com.morcinek.players.core.data

import com.google.firebase.database.IgnoreExtraProperties
import java.time.Year
import java.util.*

@IgnoreExtraProperties
class PlayerData(var name: String, var surname: String, var birthDate: Date, var photoUrl: String?) : FBData {
    override fun toMap(): Map<String, Any?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}