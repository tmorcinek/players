package com.morcinek.players.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.morcinek.players.AppPreferences
import com.morcinek.players.R
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.teamsLiveDataForValueListener
import com.morcinek.players.core.extensions.observe
import com.morcinek.players.core.extensions.startActivityForResult
import com.morcinek.players.core.extensions.startNewActivityFinishCurrent
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class SplashActivity : AppCompatActivity() {

    private val auth by inject<FirebaseAuth>()
    private val viewModel by viewModel<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (auth.currentUser) {
            null -> startSignInActivity()
            else -> startNextActivity()
        }
    }

    private fun startSignInActivity() = startActivityForResult(
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(
                arrayListOf(
                    AuthUI.IdpConfig.GoogleBuilder().build(),
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.AnonymousBuilder().build()
                )
            )
            .setTheme(R.style.AppTheme_NoActionBar_Splash)
            .build()
    )

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) startNextActivity() else finish()
    }

    private fun startNextActivity() {
        if (viewModel.hasSelectedTeam){
            startNewActivityFinishCurrent<NavActivity>()
        } else {
            observe(viewModel.teams) {
                viewModel.appPreferences.selectedTeamData = it.first()
                startNewActivityFinishCurrent<NavActivity>()
            }
        }
    }
}

private class SplashViewModel(val references: FirebaseReferences, val appPreferences: AppPreferences) : ViewModel() {

    val teams = references.teamsLiveDataForValueListener()

    val hasSelectedTeam: Boolean get() = appPreferences.selectedTeamData != null
}


val splashModule = module {
    viewModel { SplashViewModel(get(), get()) }
}
