package com.morcinek.players.core.extensions

import com.google.firebase.database.DataSnapshot

inline fun <reified T> DataSnapshot.getValue(): T = getValue(T::class.java)!!
