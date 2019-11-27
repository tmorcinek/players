package com.morcinek.players.core.database

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations

inline fun <reified T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observer: (T) -> Unit) = observe(owner, Observer { observer(it) })

fun <X, Y> LiveData<X>.map(mapFunction: (X) -> (Y)): LiveData<Y> = Transformations.map(this, mapFunction)
