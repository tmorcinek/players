package com.morcinek.core

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions


fun NavController.navigate(@IdRes resId: Int, args: Bundle? = null, optionsBuilder: NavOptionsBuilder.() -> Unit) = navigate(resId, args, navOptions(optionsBuilder))
