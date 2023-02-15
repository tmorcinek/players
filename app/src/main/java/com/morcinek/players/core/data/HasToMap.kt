package com.morcinek.players.core.data

import com.morcinek.android.HasKey


interface HasToMap {
    fun toMap(): Map<String, Any?>
}

interface FirebaseKey : HasKey {
    override var key: String
}