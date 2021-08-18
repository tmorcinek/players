package com.morcinek.players.core.extensions

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment


inline fun <reified T : Parcelable> Fragment.parcelable(key: String = T::class.java.name) = lazy { getParcelable<T>(key) }

inline fun <reified T : Parcelable> Fragment.getParcelable(key: String = T::class.java.name): T = getParcelableOrNull(key)!!
inline fun <reified T : Parcelable> Fragment.getParcelableOrNull(key: String = T::class.java.name): T? = arguments?.getParcel(key)

inline fun <reified T : Parcelable> Bundle.getParcel(key: String = T::class.java.name): T? = getParcelable(key)

fun <T : Parcelable> Bundle.putParcel(value: T, key: String = value.javaClass.name) = putParcelable(key, value)
inline fun <reified T : Parcelable> Bundle.putTypedParcel(value: Parcelable) = putParcelable(T::class.java.name, value)

fun Bundle.putString(value: String) = putString(String::class.java.name, value)
fun Bundle.getString() = getString(String::class.java.name)

fun Fragment.getString() = arguments?.getString(String::class.java.name)!!

fun Bundle.putInt(value: Int) = putInt(Int::class.java.name, value)
fun Fragment.getIntOrNull() = arguments?.getInt(Int::class.java.name, -1).takeIf { it != -1 }

inline fun <reified T : Parcelable> Intent.putParcel(value: T, key: String = value.javaClass.name) = putExtra(key, value)
inline fun <reified T : Parcelable> Intent.getParcel(key: String = T::class.java.name): T = getParcelableExtra(key)!!
inline fun <reified T : Parcelable> Intent.getParcelOrNull(key: String = T::class.java.name): T? = getParcelableExtra(key)

fun Parcelable.toBundle() = Bundle().apply { putParcel(this@toBundle) }
fun <T: Parcelable> T.toBundleWithTitle( title: T.() -> String) = Bundle().apply {
    putParcel(this@toBundleWithTitle)
    putString("title", title())
}
fun bundle(vararg parcelable: Parcelable) = Bundle().apply { parcelable.forEach { putParcel(it) } }
fun bundle(body: Bundle.() -> Unit) = Bundle().apply(body)