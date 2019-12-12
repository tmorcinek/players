package com.morcinek.players.ui.funino.create

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.morcinek.players.R
import com.morcinek.players.core.*
import com.morcinek.players.core.database.map
import com.morcinek.players.core.database.mutableSetValueLiveData
import com.morcinek.players.core.database.observe
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.setDrawableColor
import com.morcinek.players.core.extensions.toBundle
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.ui.funino.TeamData
import com.morcinek.players.ui.funino.TournamentData
import com.morcinek.players.ui.funino.TournamentDetailsData
import com.morcinek.players.ui.funino.TournamentGameData
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_number_games.view.nextButton
import kotlinx.android.synthetic.main.fragment_number_games.view.recyclerView
import kotlinx.android.synthetic.main.fragment_select_colors.view.*
import kotlinx.android.synthetic.main.vh_color.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import kotlin.collections.set

class WhatColorsFragment : BaseFragment(R.layout.fragment_select_colors) {

    private val navController: NavController by lazyNavController()

    private val viewModel by viewModelWithFragment<WhatColorsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = SelectionListAdapter<Color>(R.layout.vh_color, itemCallback { areItemsTheSame { i1, i2 -> i1.code == i2.code } },
                MultiSelect(viewModel.requiredNumberOfColors)
            ) { _, item ->
                text.text = item.name
                image.setDrawableColor(item.code)
            }.apply {
                selectedItems = viewModel.selectedColors.value!!
                observe(viewModel.colors) { submitList(it) }
                onSelectedItemsChanged { viewModel.selectedColors.postValue(it) }
            }
        }
        view.selectedColorsRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            adapter = listAdapter<Color>(R.layout.vh_color_selected, itemCallback { areItemsTheSame { i1, i2 -> i1.code == i2.code } }) { _, item ->
                text.text = item.name
                image.setDrawableColor(item.code)
            }.apply {
                observe(viewModel.selectedColors) { submitList(it.toList()) }
            }
        }
        observe(viewModel.isNextEnabled) { view.nextButton.isEnabled = it }
        view.nextButton.setOnClickListener {
            val colors = viewModel.selectedColors.value!!.map { it to 0 }.toMap().toMutableMap()
            val list = viewModel.createTournamentData.players.toList().let { players ->
                viewModel.createTournamentData.let { data ->
                    data.games.mapIndexed { index, pair ->
                        val takenColors = colors.toList().sortedBy { it.second }.take(2)
                        takenColors.forEach { colors[it.first] = it.second + 1 }

                        TournamentGameData(
                            index,
                            TeamData(takenColors[0].first.code, pair.first.map { players[it] }.toSet()),
                            TeamData(takenColors[1].first.code, pair.second.map { players[it] }.toSet()),
                            null
                        )
                    }
                }
            }

            val tournamentDetailsData =
                TournamentDetailsData(TournamentData(1, "Tuesday 12 Listopada", "12 players", "Not Finished", true), list)
            navController.navigate(R.id.nav_tournament_details, tournamentDetailsData.toBundle())
        }
    }
}

val whatColorsModule = module {
    viewModel { (fragment: Fragment) -> WhatColorsViewModel(fragment.getParcelable()) }
}

private class WhatColorsViewModel(val createTournamentData: CreateTournamentData) : ViewModel() {

    val selectedColors = mutableSetValueLiveData<Color>()

    val isNextEnabled: LiveData<Boolean> = selectedColors.map { it.size == requiredNumberOfColors }

    val requiredNumberOfColors: Int = if (createTournamentData.numberOfPlayers >= 12) 4 else 2

    val colors: LiveData<List<Color>> = MutableLiveData<List<Color>>().apply {
        value = listOf(
            0xFFA93226 to "Czerwony",
            0xFFFDFEFE to "Bialy",
            0xFFF4D03F to "Żółty",
            0xFF1A5276 to "Niebieski",
            0xFFE67E22 to "Pomaranczowy",
            0xFF196F3D to "Zielony",
            0xFF633974 to "Fioletowy"
        ).map { Color(it.first.toInt(), it.second) }
    }
}

@Parcelize
class Color(val code: Int, val name: String) : Parcelable