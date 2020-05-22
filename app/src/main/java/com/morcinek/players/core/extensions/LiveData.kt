package com.morcinek.players.core.extensions

import androidx.lifecycle.*

inline fun <reified T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observer: (T) -> Unit) = observe(owner, Observer { observer(it) })

inline fun <reified T> LifecycleOwner.observe(liveData: LiveData<T>, crossinline observer: (T) -> Unit) = liveData.observe(this, Observer { observer(it) })

fun <X, Y> LiveData<X>.map(mapFunction: (X) -> (Y)): LiveData<Y> = Transformations.map(this, mapFunction)

fun <T, R> combine(sourceA: LiveData<T>, sourceB: LiveData<R>): LiveData<Pair<T, R>> = MediatorLiveData<Pair<T, R>>().apply {

    var valueA: T? = null
    var valueB: R? = null

    fun update() {
        if (valueA != null && valueB != null) {
            value = valueA!! to valueB!!
        }
    }
    addSource(sourceA) {
        valueA = it
        update()
    }
    addSource(sourceB) {
        valueB = it
        update()
    }
}

fun <T, R, Y> combine(sourceA: LiveData<T>, sourceB: LiveData<R>, mapFunction: (T, R) -> (Y)): LiveData<Y> =
    combine(sourceA, sourceB).map { mapFunction(it.first, it.second) }

fun <T, R, Y> LiveData<T>.combineWith(source: LiveData<R>, mapFunction: (T, R) -> (Y)): LiveData<Y> =
    combine(this, source).map { mapFunction(it.first, it.second) }

fun <T> mutableSetValueLiveData() = MutableLiveData<Set<T>>(setOf())
