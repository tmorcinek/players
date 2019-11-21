package com.morcinek.players.ui.players.details

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import kotlinx.android.synthetic.main.fragment_player.view.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class PlayerFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_player

    private val viewModel by viewModel<PlayerViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.text.observe(this, Observer {
            view.text.text = "${it.name} ${it.surname}"
        })
    }
}

val playerModule = module {
    viewModel { PlayerViewModel() }
}