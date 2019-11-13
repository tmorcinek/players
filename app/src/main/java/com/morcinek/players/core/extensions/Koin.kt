package com.morcinek.players.core.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

inline fun <reified T : ViewModel> Fragment.viewModelWithFragment() = viewModel<T> { parametersOf(this) }
