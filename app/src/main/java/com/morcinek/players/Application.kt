package com.morcinek.players

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.ui.navModule
import com.morcinek.players.ui.players.createPlayerModule
import com.morcinek.players.ui.players.playerDetailsModule
import com.morcinek.players.ui.players.playersModule
import com.morcinek.players.ui.teams.addPlayersModule
import com.morcinek.players.ui.teams.createTeamModule
import com.morcinek.players.ui.teams.teamDetailsModule
import com.morcinek.players.ui.teams.event.createEventModule
import com.morcinek.players.ui.teams.event.eventDetailsModule
import com.morcinek.players.ui.teams.stats.playerStatsModule
import com.morcinek.players.ui.teams.teamsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@Application)
            modules(
                appModule, navModule,
                teamsModule, teamDetailsModule, createTeamModule, addPlayersModule,
                createEventModule, eventDetailsModule,
                playersModule, playerDetailsModule, createPlayerModule, playerStatsModule,
            )
        }
    }
}

val appModule = module {

    //    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(androidApplication()) }
    single { FirebaseAuth.getInstance() }
    single { FirebaseDatabase.getInstance() }
    single { FirebaseReferences(get(), get()) }
}