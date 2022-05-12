package com.morcinek.players

import android.content.SharedPreferences
import com.google.gson.Gson
import com.morcinek.players.core.data.TeamData


class AppPreferences(private val appPrefs: SharedPreferences, private val gson: Gson) {

    private fun getSharedPrefsEdit(): SharedPreferences.Editor = appPrefs.edit()

    var selectedTeamData: TeamData?
        get() = gson.fromJson<TeamData>(appPrefs.getString("TeamData", null))
        set(value) = getSharedPrefsEdit().putString("TeamData", gson.toJson(value)).apply()

}

private inline fun <reified T> Gson.fromJson(json: String?) = fromJson(json, T::class.java)