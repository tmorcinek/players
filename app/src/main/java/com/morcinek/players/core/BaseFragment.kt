package com.morcinek.players.core

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.morcinek.players.R
import com.morcinek.players.core.extensions.hide
import com.morcinek.players.core.extensions.safeLet
import com.morcinek.players.ui.NavActivity

abstract  class BaseFragment<T : ViewBinding>(private val createBinding: (LayoutInflater, ViewGroup?, Boolean) -> T) : Fragment() {

    open val menuConfiguration: MenuConfiguration? = null
    open val fabConfiguration: FabConfiguration? = null

    private var _binding: T? = null

    internal val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        createBinding(inflater, container, false).also {
            _binding = it
        }.root

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        (requireActivity() as NavActivity).binding.navigationContent.fab.let { fab ->
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
                menuConfigurationItem.action.invoke(item)
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

fun createFabConfiguration(fabIcon: Int = R.drawable.ic_add, fabActon: (View) -> Unit) = FabConfiguration(fabActon, fabIcon)

class MenuConfiguration {
    internal val actions = mutableListOf<MenuConfigurationItem>()

    fun addAction(textRes: Int, iconRes: Int, action: (MenuItem) -> Unit) {
        actions.add(MenuConfigurationItem(textRes, iconRes, action))
    }
}

class MenuConfigurationItem(val textRes: Int, val iconRes: Int, val action: (MenuItem) -> Unit)

inline fun createMenuConfiguration(function: MenuConfiguration.() -> Unit) = MenuConfiguration().apply(function)