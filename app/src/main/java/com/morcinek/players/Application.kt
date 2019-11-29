package com.morcinek.players

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.ui.funino.creator.howManyGamesModule
import com.morcinek.players.ui.funino.creator.whatColorsModule
import com.morcinek.players.ui.funino.creator.whichPlayersModule
import com.morcinek.players.ui.funino.details.tournamentDetailsModule
import com.morcinek.players.ui.funino.funinoModule
import com.morcinek.players.ui.players.create.createPlayerModule
import com.morcinek.players.ui.players.details.playerDetailsModule
import com.morcinek.players.ui.players.playersModule
import com.morcinek.players.ui.teams.addPlayers.addPlayersModule
import com.morcinek.players.ui.teams.create.createTeamModule
import com.morcinek.players.ui.teams.details.teamDetailsModule
import com.morcinek.players.ui.teams.event.createEventModule
import com.morcinek.players.ui.teams.event.eventDetailsModule
import com.morcinek.players.ui.teams.teamsModule
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
                teamsModule, teamDetailsModule, createTeamModule, addPlayersModule,
                createEventModule, eventDetailsModule,
                playersModule, playerDetailsModule, createPlayerModule,
                funinoModule, tournamentDetailsModule,
                whichPlayersModule, howManyGamesModule, whatColorsModule
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