package com.morcinek.players

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.morcinek.players.ui.player.playerModule
import com.morcinek.players.ui.players.playersModule
import com.morcinek.players.ui.teams.details.teamDetailsModule
import com.morcinek.players.ui.teams.teamsModule
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(
                appModule,
                teamsModule, playersModule,
                playerModule, teamDetailsModule
            )
        }
    }
}

val appModule = module {

    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(androidApplication()) }
}