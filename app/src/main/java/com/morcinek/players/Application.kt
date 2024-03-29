package com.morcinek.players

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.GsonBuilder
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.ui.createFirstTeamModule
import com.morcinek.players.ui.navModule
import com.morcinek.players.ui.players.createPlayerModule
import com.morcinek.players.ui.players.playerDetailsModule
import com.morcinek.players.ui.players.playersModule
import com.morcinek.players.ui.splashModule
import com.morcinek.players.ui.team.createTeamModule
import com.morcinek.players.ui.team.eventsModule
import com.morcinek.players.ui.team.teamInfoModule
import com.morcinek.players.ui.team.teamStatsModule
import com.morcinek.players.ui.teams.addPlayersModule
import com.morcinek.players.ui.teams.event.createEventModule
import com.morcinek.players.ui.teams.event.createPointsModule
import com.morcinek.players.ui.teams.event.eventDetailsModule
import com.morcinek.players.ui.teams.stats.filterStatsModule
import com.morcinek.players.ui.teams.stats.playerStatsModule
import com.morcinek.players.ui.teams.stats.teamDetailsModule
import com.morcinek.players.ui.teams.teamsModule
import org.koin.android.ext.koin.androidApplication
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
                splashModule,
                createFirstTeamModule,
                appModule, navModule,
                teamsModule, eventsModule, teamStatsModule,
                teamInfoModule,
                teamDetailsModule, createTeamModule, addPlayersModule, filterStatsModule,
                createEventModule, eventDetailsModule, createPointsModule,
                playersModule, playerDetailsModule, createPlayerModule, playerStatsModule,
            )
        }
    }
}

val appModule = module {

    factory { GsonBuilder().create() }
    factory { androidApplication().getSharedPreferences("AppSharedPreferences", Context.MODE_PRIVATE) }
    factory { AppPreferences(get(), get()) }

    single { FirebaseAuth.getInstance() }
    single { FirebaseDatabase.getInstance() }
    single { FirebaseReferences(get(), get()) }
}