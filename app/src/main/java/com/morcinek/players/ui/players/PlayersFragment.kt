package com.morcinek.players.ui.players

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.FabConfiguration
import com.morcinek.players.core.SimpleListAdapter
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.getList
import com.morcinek.players.core.database.getPlayers
import com.morcinek.players.core.database.valueEventListener
import com.morcinek.players.core.itemCallback
import com.morcinek.players.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_list.*
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
        view.apply {
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = PlayersAdapter().apply {
                viewModel.players.observe(this@PlayersFragment, Observer { submitList(it) })
            }
        }
    }
}

class PlayersAdapter : SimpleListAdapter<PlayerData>(itemCallback()) {

    override val vhResourceId = R.layout.vh_player

    override fun onBindViewHolder(item: PlayerData, view: View) {
        view.name.text = "${item.name} ${item.surname}"
        view.subtitle.text = item.id
    }
}

class PlayersViewModel(references: FirebaseReferences) : ViewModel() {

    val players: LiveData<List<PlayerData>> = MutableLiveData<List<PlayerData>>().apply {
        references.playersReference().addValueEventListener(valueEventListener { postValue(it.getPlayers()) })
    }
}

val playersModule = module {
    viewModel { PlayersViewModel(get()) }
}