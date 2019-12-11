package com.morcinek.players.core.extensions


inline fun <A, B, R> combineTwo(a: A, b: B, function: (A, B) -> R) = function(a, b)

inline fun <T> T?.safeLet(block: (T) -> Unit) = this?.let { block(it) } ?: Unit


