package com.morcinek.players.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.morcinek.players.R
import kotlinx.android.synthetic.main.app_bar_main.*

abstract class BaseFragment : Fragment() {

    protected abstract val layoutResourceId: Int

    open val fabConfiguration: FabConfiguration? = null

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(layoutResourceId, container, false)!!

    override fun onResume() {
        super.onResume()

        fabConfiguration?.let {
            requireActivity().fab.apply {
                setImageResource(it.fabIcon)
                setOnClickListener(it.fabActon)
                if (isShown) hide()
                show()
            }

        } ?: requireActivity().fab.hide()
    }
}

class FabConfiguration(val fabActon: (View) -> Unit, val fabIcon: Int = R.drawable.ic_menu_send)