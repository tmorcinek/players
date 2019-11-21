package com.morcinek.players.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.morcinek.players.R
import com.morcinek.players.core.extensions.startActivity
import com.morcinek.players.core.extensions.startActivityForResult

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (FirebaseAuth.getInstance().currentUser) {
            null -> startSignInActivity()
            else -> finishAndStartNextActivity()
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

    private fun finishAndStartNextActivity() {
        startActivity<NavActivity>()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            finishAndStartNextActivity()
        } else {
            finish()
        }
    }
}