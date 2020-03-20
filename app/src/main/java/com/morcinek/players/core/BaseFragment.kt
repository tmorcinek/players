package com.morcinek.players.core

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.morcinek.players.R
import com.morcinek.players.core.extensions.hide
import com.morcinek.players.core.extensions.safeLet
import kotlinx.android.synthetic.main.app_bar_main.*

abstract class BaseFragment(private val layoutResourceId: Int) : Fragment() {

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
        menuConfiguration?.takeIf { item.itemId < it.actions.size }?.let {
            it.actions[item.itemId].let { menuConfigurationItem ->
                menuConfigurationItem.action.invoke()
                return true
            }
        } ?: return false
    }

    final override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) = menuConfiguration.safeLet {
        it.actions.forEachIndexed { index, item ->
            menu.add(Menu.NONE, index, index, item.textRes)
                .setIcon(item.iconRes)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
    }
}

class FabConfiguration(val fabActon: (View) -> Unit, val fabIcon: Int = R.drawable.ic_add)

class MenuConfiguration {
    internal val actions = mutableListOf<MenuConfigurationItem>()

    fun addAction(textRes: Int, iconRes: Int, action: () -> Any) {
        actions.add(MenuConfigurationItem(textRes, iconRes, action))
    }
}

class MenuConfigurationItem(val textRes: Int, val iconRes: Int, val action: () -> Any)

inline fun createMenuConfiguration(function: MenuConfiguration.() -> Unit) = MenuConfiguration().apply(function)