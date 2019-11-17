package com.morcinek.players.ui.funino.creator

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.extensions.toBundle
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_number.view.*

class HowManyPlayersFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_number

    private val navController: NavController by lazyNavController()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.numberPicker.apply {
            minValue = 6
            maxValue = 20
        }
        view.nextButton.setOnClickListener { navController.navigate(R.id.nav_how_many_games, CreateTournamentData(view.numberPicker.value).toBundle()) }
    }
}