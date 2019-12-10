package com.morcinek.players.core.extensions


fun <A, B, R> combineTwo(a: A, b: B, function: (A, B) -> R) = function(a, b)