package com.morcinek.players.ui.players

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.FabConfiguration
import com.morcinek.players.core.clickableListAdapter
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.observe
import com.morcinek.players.core.database.playersLiveDataForValueListener
import com.morcinek.players.core.extensions.toBundle
import com.morcinek.players.core.extensions.toStandardString
import com.morcinek.players.core.itemCallback
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.vh_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class PlayersFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_list

    private val viewModel by viewModel<PlayersViewModel>()

    private val navController: NavController by lazyNavController()

    override val fabConfiguration = FabConfiguration({ navController.navigate(R.id.nav_create_player) })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.progressBar.show()
        view.apply {
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = clickableListAdapter(R.layout.vh_player, itemCallback()) { item: PlayerData, view ->
                view.name.text = item.toString()
                view.subtitle.text = item.teamKey
                view.date.text = item.getBirthDate().toStandardString()
            }.apply {
                viewModel.players.observe(this@PlayersFragment) {
                    submitList(it.sortedBy(PlayerData::teamKey))
                    view.progressBar.hide()
                }
                onClickListener { _, playerData -> navController.navigate(R.id.nav_player_details, playerData.toBundle()) }
            }
        }
    }
}

class PlayersViewModel(references: FirebaseReferences) : ViewModel() {

    val players = references.playersLiveDataForValueListener()
}

val playersModule = module {
    viewModel { PlayersViewModel(get()) }
}