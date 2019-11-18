package com.morcinek.players.ui.funino.creator

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.ClickableListAdapter
import com.morcinek.players.core.SimpleListAdapter
import com.morcinek.players.core.extensions.getParcelable
import com.morcinek.players.core.extensions.setDrawableColor
import com.morcinek.players.core.extensions.viewModelWithFragment
import com.morcinek.players.core.itemCallback
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_select_colors.view.*
import kotlinx.android.synthetic.main.fragment_number_games.view.nextButton
import kotlinx.android.synthetic.main.fragment_number_games.view.recyclerView
import kotlinx.android.synthetic.main.vh_color.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class WhatColorsFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_select_colors

    private val navController: NavController by lazyNavController()

    private val viewModel by viewModelWithFragment<WhatColorsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = WhatColorsAdapter().apply {
                viewModel.colors.observe(this@WhatColorsFragment, Observer { submitList(it) })
                viewModel.selectedColors.observe(this@WhatColorsFragment, Observer {
                    selectedItems = it
                    notifyDataSetChanged()
                })
                onClickListener { _, item -> viewModel.select(item) }
            }
        }
        view.selectedColorsRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            adapter = SelectedColorsAdapter().apply {
                viewModel.selectedColors.observe(this@WhatColorsFragment, Observer { submitList(it.toList()) })
            }
        }
        viewModel.isNextEnabled.observe(this, Observer { view.nextButton.isEnabled = it })
        view.nextButton.setOnClickListener {
            Toast.makeText(requireContext(), "Next Button", Toast.LENGTH_SHORT).show()
//            navController.navigate()
        }
    }
}

private class WhatColorsAdapter : ClickableListAdapter<Color>(itemCallback {
    areItemsTheSame { oldItem, newItem -> oldItem.code == newItem.code }
}) {

    var selectedItems: Set<Color> = setOf()

    override val vhResourceId = R.layout.vh_color

    override fun onBindViewHolder(item: Color, view: View) {
        super.onBindViewHolder(item, view)
        view.isSelected = item in selectedItems
        view.text.text = item.name
        view.image.setDrawableColor(item.code)
    }
}

private class SelectedColorsAdapter : SimpleListAdapter<Color>(itemCallback {
    areItemsTheSame { oldItem, newItem -> oldItem.code == newItem.code }
}) {

    override val vhResourceId = R.layout.vh_color_selected

    override fun onBindViewHolder(item: Color, view: View) {
        view.text.text = item.name
        view.image.setDrawableColor(item.code)
    }
}

val whatColorsModule = module {
    viewModel { (fragment: Fragment) -> WhatColorsViewModel(fragment.getParcelable()) }
}

private class WhatColorsViewModel(private val createTournamentData: CreateTournamentData) : ViewModel() {

    val selectedColors: LiveData<Set<Color>> = MutableLiveData<Set<Color>>().apply { value = setOf() }

    val isNextEnabled: LiveData<Boolean> = Transformations.map(selectedColors) { it.size == requiredNumberOfColors }

    fun select(color: Color) {
        if (selectedColors.value!!.size < requiredNumberOfColors || color in selectedColors.value!!) {
            (selectedColors as MutableLiveData).postValue(updateSelectedColors(color))
        }
    }

    private val requiredNumberOfColors: Int = if (createTournamentData.numberOfPlayers >= 12) 4 else 2

    private fun updateSelectedColors(color: Color) = selectedColors.value!!.let { selectedColors ->
        if (color in selectedColors) selectedColors.minus(color) else selectedColors.plus(color)
    }

    val colors: LiveData<List<Color>> = MutableLiveData<List<Color>>().apply {
        value = listOf(
            0xFFA93226 to "Czerwony",
            0xFF633974 to "Fioletowy",
            0xFF1A5276 to "Niebieski",
            0xFF196F3D to "Zielony",
            0xFFF4D03F to "Żółty",
            0xFFE67E22 to "Pomaranczowy",
            0xFFFDFEFE to "Bialy"
            ).map { Color(it.first.toInt(), it.second) }
    }
}

class Color(val code: Int, val name: String)