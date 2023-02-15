package com.morcinek.players.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.morcinek.core.navigate
import com.morcinek.core.ui.PopupAdapter
import com.morcinek.core.ui.showPopupWindow
import com.morcinek.players.AppPreferences
import com.morcinek.players.R
import com.morcinek.players.core.data.TeamData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.teamsLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.core.extensions.alert.alert
import com.morcinek.players.core.extensions.alert.noButton
import com.morcinek.players.core.extensions.alert.yesButton
import com.morcinek.players.databinding.ActivityMainBinding
import com.morcinek.players.databinding.NavHeaderMainBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class NavActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val appPreferences by inject<AppPreferences>()

    private val viewModel by viewModel<NavViewModel>()

    private val appBarConfiguration by lazy {
        AppBarConfiguration(
            setOf(R.id.nav_players, R.id.nav_events),
            binding.drawerLayout
        )
    }

    private val headerBinding: NavHeaderMainBinding get() = NavHeaderMainBinding.bind(binding.navigationView.getHeaderView(0))

    private val navController by lazy { findNavController(R.id.navHostFragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        setSupportActionBar(binding.navigationContent.toolbar)
        viewModel.user.let { currentUser ->
            headerBinding.run {
                if (currentUser.isAnonymous) {
                    navHeaderTitle.setText(R.string.nav_header_title)
                    navHeaderSubtitle.setText(R.string.nav_header_subtitle)
                } else {
                    Glide.with(this@NavActivity)
                        .load(currentUser.photoUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView)
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
                                viewModel.signOut()
                                startNewActivityFinishCurrent<SplashActivity>()
                            }
                            noButton {}
                        }.show()
                    }
                }
                editTeams.setOnClickListener {
                    navController.navigate(R.id.nav_teams) { launchSingleTop = true }
                    binding.drawerLayout.closeDrawers()
                }
                selectTeamButton.run {
                    text = appPreferences.selectedTeamData?.name ?: "Select team"
//                    icon.rotate180()
                    setOnClickListener {
                        observe(viewModel.teams) { teams ->
                            it.showPopupWindow(
                                PopupAdapter(teams, R.layout.vh_team_dropdown) {
                                    (this as TextView).text = it.name
                                },
                                onItemSelected = {
                                    appPreferences.selectedTeamData = it
                                    text = appPreferences.selectedTeamData?.name
                                    onTeamDataSelected(it)
                                    binding.drawerLayout.closeDrawers()
                                },
//                            onDismissed = { icon.rotate180() }
                            )

                        }
                    }
                }
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navigationView.apply {
            setupWithNavController(navController)
//            menu.findItem(R.id.teams).subMenu.let { menu ->
//                observe(viewModel.teams) {
//                    menu.clear()
//                    it.forEach { team ->
//                        menu.add(team.name, R.drawable.ic_menu_players) {
//                            navController.navigateSingleTop(R.id.nav_team_details, team.toBundleWithTitle { name })
//                            drawerLayout.closeDrawers()
//                        }
//                    }
//                }
//            }
        }
        appPreferences.selectedTeamData?.let { navController.navigate(R.id.nav_team_details, it.toBundleWithTitle { name }) { popUpTo(R.id.nav_teams) { inclusive = true } } }
    }

    private fun onTeamDataSelected(it: TeamData) {
        navController.navigate(R.id.nav_team_details, it.toBundleWithTitle { name }) { popUpTo(R.id.nav_team_details) { inclusive = true } }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            startNewActivityFinishCurrent<SplashActivity>()
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
}

fun Fragment.lazyNavController() = lazy { Navigation.findNavController(view!!) }

private class NavViewModel(references: FirebaseReferences, val auth: FirebaseAuth) : ViewModel() {

    val teams = references.teamsLiveDataForValueListener()

    val user: FirebaseUser
        get() = auth.currentUser!!

    fun signOut() = auth.signOut()
}

val navModule = module {
    viewModel { NavViewModel(get(), get()) }
}