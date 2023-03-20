package com.morcinek.core

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.*
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigator
import androidx.navigation.navOptions
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment


fun Fragment.recreateActivity() = requireActivity().recreate()

fun FragmentActivity.popBackFragment() = supportFragmentManager.popBackStack()
fun Fragment.popBackFragment() = requireActivity().popBackFragment()

fun FragmentActivity.popBackAllFragments(name: String? = null) = supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)

inline fun <reified F : Fragment> FragmentActivity.replaceFragment(@IdRes containerViewId: Int, args: Bundle? = null) {
    if (!supportFragmentManager.hasFragment<F>()) {
        supportFragmentManager.commit {
            replace<F>(containerViewId, tag<F>(), args)
        }
    }
}

fun <F : Fragment> FragmentActivity.replaceFragment(@IdRes containerViewId: Int, fragmentClass: Class<F>, args: Bundle? = null) {
    supportFragmentManager.commit {
        replace(containerViewId, fragmentClass, args, fragmentClass.name)
    }
}

inline fun <reified F : Fragment> FragmentActivity.pushFragment(
    @IdRes containerViewId: Int,
    args: Bundle? = null,
    transitionAnimation: TransitionAnimation? = null,
    backStackName: String = tag<F>(),
) {
    if (!supportFragmentManager.hasFragment<F>()) {
        supportFragmentManager.commit {
            transitionAnimation?.let { setCustomAnimations(it.createEnter, it.createExit, it.finishEnter, it.finishExit) }
            replace<F>(containerViewId, tag<F>(), args)
            addToBackStack(backStackName)
        }
    }
}

inline fun <reified F : Fragment> Fragment.pushFragment(@IdRes containerViewId: Int, args: Bundle? = null, transitionAnimation: TransitionAnimation? = null) =
    requireActivity().pushFragment<F>(containerViewId, args, transitionAnimation)

inline fun <reified F : Fragment> tag(): String = F::class.java.name
inline fun <reified F : Fragment> FragmentManager.findFragment() = findFragmentByTag(tag<F>())
inline fun <reified F : Fragment> FragmentManager.hasFragment() = findFragment<F>() != null

sealed class TransitionAnimation(
    @AnimRes val createEnter: Int, @AnimRes val createExit: Int,
    @AnimRes val finishEnter: Int, @AnimRes val finishExit: Int,
)

//object SideSlideTransitionAnimation : TransitionAnimation(R.anim.slide_in_left, android.R.anim.fade_out, android.R.anim.fade_in, R.anim.slide_out_right)
//object BottomSlideTransitionAnimation : TransitionAnimation(R.anim.slide_bottom_in, R.anim.slide_top_shrink, R.anim.slide_top_expand, R.anim.slide_bottom_out)

class NavController(activity: AppCompatActivity, @IdRes val containerViewId: Int) {

    val supportFragmentManager by lazy { activity.supportFragmentManager }

    inline fun <reified F : Fragment> navigate(args: Bundle? = null, navOptions: NavOptions? = null, navigatorExtras: Navigator.Extras? = null) {
//        if (!supportFragmentManager.hasFragment<F>()) {
            supportFragmentManager.commit {
//                transitionAnimation?.let { setCustomAnimations(it.createEnter, it.createExit, it.finishEnter, it.finishExit) }
                replace<F>(containerViewId, tag<F>(), args)
                addToBackStack(tag<F>())
            }
//        }
    }

    inline fun <reified F : Fragment> navigate(args: Bundle? = null, noinline optionsBuilder: NavOptionsBuilder.() -> Unit) = navigate<F>(args, navOptions(optionsBuilder))

    fun popBackStack() { supportFragmentManager.popBackStack() }
    fun navigateUp(appBarConfiguration: Any) = if (supportFragmentManager.backStackEntryCount > 1) {
        supportFragmentManager.popBackStack(); true
    } else {
        false
    }
}

interface NavControllerHost {
    val navController: NavController
}

fun Fragment.lazyNavController() = lazy { (activity as NavControllerHost).navController }


class ToolbarNavController(
    activity: AppCompatActivity,
    drawerLayout: DrawerLayout,
    toolbar: Toolbar,
    @StringRes openDrawerContentDescRes: Int,
    @StringRes closeDrawerContentDescRes: Int,
) {

    private var arrowDrawable: DrawerArrowDrawable? = null
    private var animator: ValueAnimator? = null

    init {

        activity.supportFragmentManager.run {
            toolbar.setNavigationOnClickListener {
                when {
                    drawerLayout.isOpen -> drawerLayout.closeDrawers()
                    backStackEntryCount == 1 -> drawerLayout.open()
                    else -> popBackStack()
                }
            }

            addOnBackStackChangedListener {
                when (backStackEntryCount) {
                    0 -> activity.finish()
                    else -> {
                        val fragment = findFragmentByTag(getBackStackEntryAt(backStackEntryCount - 1).name) as BaseFragment<*>
                        toolbar.setTitle(fragment.title)
                        val showAsDrawerIndicator = backStackEntryCount == 1
                        val (arrow, animate) = arrowDrawable?.run { this to true } ?: (DrawerArrowDrawable(activity).apply { color = activity.getColor(R.color.white) }.also { arrowDrawable = it } to false)
                        toolbar.run {
                            navigationIcon = arrow
                            setNavigationContentDescription(if (showAsDrawerIndicator) openDrawerContentDescRes else closeDrawerContentDescRes)
                            val endValue = if (showAsDrawerIndicator) 0f else 1f
                            if (animate) {
                                val startValue = arrow.progress
                                animator?.cancel()
                                animator = ObjectAnimator.ofFloat(arrow, "progress", startValue, endValue)
                                (animator as ObjectAnimator).start()
                            } else {
                                arrow.progress = endValue
                            }
                        }
                    }
                }
            }
        }
    }
}
