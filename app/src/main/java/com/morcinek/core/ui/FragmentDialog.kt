package com.morcinek.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

inline fun <reified T : DialogFragment> T.show(fragmentManager: FragmentManager) = show(fragmentManager, T::class.java.name)

inline fun <reified T : DialogFragment> FragmentActivity.showFragmentDialog() = T::class.java.newInstance().show(supportFragmentManager)
inline fun <reified T : DialogFragment> Fragment.showFragmentDialog() = requireActivity().showFragmentDialog<T>()

abstract class BindingDialogFragment<T : ViewBinding>(private val createBinding: (LayoutInflater, ViewGroup?, Boolean) -> T) : BottomSheetDialogFragment() {

    abstract fun onBindingView(binding: T)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = createBinding(inflater, container, false).also {
        onBindingView(it)
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawable(null)
        super.onViewCreated(view, savedInstanceState)
    }
}