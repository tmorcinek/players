package com.morcinek.players.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.morcinek.players.R
import com.morcinek.players.core.extensions.hide
import kotlinx.android.synthetic.main.app_bar_main.*

abstract class BaseFragment : Fragment() {

    protected abstract val layoutResourceId: Int

    open val fabConfiguration: FabConfiguration? = null

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(layoutResourceId, container, false)!!

    override fun onResume() {
        super.onResume()

        requireActivity().fab.let { fab ->
            if (fab.isOrWillBeShown) fab.hide {
                initializeFab(fab)
            } else {
                initializeFab(fab)
            }
        }
    }

    private fun initializeFab(fab: FloatingActionButton) {
        fabConfiguration?.let {
            fab.apply {
                setImageResource(it.fabIcon)
                setOnClickListener(it.fabActon)
                show()
            }
        }
    }
}

class FabConfiguration(val fabActon: (View) -> Unit, val fabIcon: Int = R.drawable.ic_add)