package com.morcinek.players

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.morcinek.players.ui.gallery.galleryModule
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
                galleryModule
            )
        }
    }
}

val appModule = module {

    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(androidApplication()) }
}