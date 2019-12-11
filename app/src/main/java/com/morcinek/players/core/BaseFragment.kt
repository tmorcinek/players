package com.morcinek.players.core

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.morcinek.players.R
import com.morcinek.players.core.extensions.hide
import kotlinx.android.synthetic.main.app_bar_main.*

abstract class BaseFragment : Fragment() {

    protected abstract val layoutResourceId: Int

    open val menuConfiguration: MenuConfiguration? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuConfiguration?.let { setHasOptionsMenu(true) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        menuConfiguration?.let {
            it.actions[item.itemId]?.let { action ->
                action.invoke()
                return true
            }
        } ?: return false
    }

    final override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menuConfiguration?.let { inflater.inflate(it.menuResourceId, menu) }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menuConfiguration?.let { it.prepare.forEach { entry -> entry.value(menu.findItem(entry.key))  } }
    }
}

class FabConfiguration(val fabActon: (View) -> Unit, val fabIcon: Int = R.drawable.ic_add)

class MenuConfiguration(val menuResourceId: Int) {
    internal var actions = mutableMapOf<Int, () -> Any>()
    internal var prepare = mutableMapOf<Int, (MenuItem) -> Unit>()

    fun addAction(itemId: Int, action: () -> Any) {
        actions[itemId] = action
    }

    fun addPrepare(itemId: Int, action: (MenuItem) -> Unit) {
        prepare[itemId] = action
    }
}

fun BaseFragment.createMenuConfiguration(menuResourceId: Int, function: MenuConfiguration.() -> Unit = {}) = MenuConfiguration(menuResourceId).apply(function)