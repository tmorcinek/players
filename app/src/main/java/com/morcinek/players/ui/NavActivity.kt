package com.morcinek.players.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.morcinek.players.R
import com.morcinek.players.core.extensions.alert.alert
import com.morcinek.players.core.extensions.alert.noButton
import com.morcinek.players.core.extensions.alert.yesButton
import com.morcinek.players.core.extensions.startActivityForResult
import com.morcinek.players.core.extensions.startNewActivityFinishCurrent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.koin.android.ext.android.inject

class NavActivity : AppCompatActivity() {

    private val auth by inject<FirebaseAuth>()

    private val appBarConfiguration by lazy {
        AppBarConfiguration(
            setOf(R.id.nav_players, R.id.nav_teams, R.id.nav_tournament),
            drawerLayout
        )
    }

    private val headerView: View
        get() = navigationView.getHeaderView(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        auth.currentUser?.let { currentUser ->
            headerView.apply {
                if (currentUser.isAnonymous) {
                    navHeaderTitle.setText(R.string.nav_header_title)
                    navHeaderSubtitle.setText(R.string.nav_header_subtitle)
                } else {
                    imageView.setImageURI(currentUser.photoUrl)
                    navHeaderTitle.text = currentUser.displayName
                    navHeaderSubtitle.text = currentUser.email
                }
                loginLayout.apply {
                    isEnabled = currentUser.isAnonymous
                    setOnClickListener {
                        startActivityForResult(
                            AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(
                                    arrayListOf(
                                        AuthUI.IdpConfig.GoogleBuilder().build(),
                                        AuthUI.IdpConfig.EmailBuilder().build()
                                    )
                                )
                                .enableAnonymousUsersAutoUpgrade()
                                .setTheme(R.style.AppTheme_NoActionBar_Splash)
                                .build()
                        )
                    }
                }
                logoutButton.apply {
                    isVisible = !currentUser.isAnonymous
                    setOnClickListener {
                        alert(R.string.logout_message) {
                            yesButton {
                                auth.signOut()
                                startNewActivityFinishCurrent<SplashActivity>()
                            }
                            noButton {}
                        }.show()
                    }
                }
            }
        }

        findNavController(R.id.navHostFragment).let { navController ->
            setupActionBarWithNavController(navController, appBarConfiguration)
            navigationView.apply {
                setupWithNavController(navController)
                menu.findItem(R.id.teams).subMenu.add("Skrzaty").setOnMenuItemClickListener {
                    navController.navigate(R.id.nav_players, Bundle(), NavOptions.Builder().setLaunchSingleTop(true).build())
                    drawerLayout.closeDrawers()
                    true
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            startNewActivityFinishCurrent<SplashActivity>()
        }
    }

    override fun onSupportNavigateUp() = findNavController(R.id.navHostFragment).navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
}

fun Fragment.findNavController(): NavController = requireActivity().findNavController(R.id.navHostFragment)

fun Fragment.lazyNavController() = lazy { findNavController() }
