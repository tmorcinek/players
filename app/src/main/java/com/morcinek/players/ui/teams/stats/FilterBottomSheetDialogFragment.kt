package com.morcinek.players.ui.teams.stats

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.morcinek.android.HasKey
import com.morcinek.android.itemCallback
import com.morcinek.android.setup
import com.morcinek.core.ui.BindingDialogFragment
import com.morcinek.players.core.data.EventData
import com.morcinek.players.core.extensions.map
import com.morcinek.players.core.extensions.updateValue
import com.morcinek.players.databinding.FragmentFilterBinding
import com.morcinek.players.databinding.VhCheckboxBinding
import com.morcinek.players.databinding.VhRadioButtonBinding

class FilterBottomSheetDialogFragment : BindingDialogFragment<FragmentFilterBinding>(FragmentFilterBinding::inflate) {

    val filterData = MutableLiveData(FilterData())

    override fun onBindingView(binding: FragmentFilterBinding) = binding.run {
        periodGroup.setup {
            adapter(com.morcinek.android.listAdapter(itemCallback<SelectableItem<FilterData.Period>>(), VhRadioButtonBinding::inflate) {
                onBind { _, item ->
                    root.run {
                        text = item.key
                        isChecked = item.isSelected
                        setOnClickListener { filterData.updateValue { copy(period = item.item) } }
                    }
                }
                liveData(viewLifecycleOwner, filterData.map { data -> FilterData.Period.values().map { SelectableItem(it, it == data.period) } })
            })
            grid(2)
        }
        typeGroup.setup {
            adapter(com.morcinek.android.listAdapter(itemCallback<SelectableItem<EventData.Type>>(), VhCheckboxBinding::inflate) {
                onBind { _, item ->
                    root.run {
                        text = item.key
                        isChecked = item.isSelected
                        setOnClickListener { filterData.updateValue { copy(types = if (isChecked) types.plus(item.item) else types.minus(item.item)) } }
                    }
                }
                liveData(viewLifecycleOwner, filterData.map { data -> EventData.Type.values().map { SelectableItem(it, it in data.types) } })
            })
            grid(2)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}

class SelectableItem<T>(val item: T, val isSelected: Boolean) : HasKey {
    override val key = item.toString()

    override fun equals(other: Any?) = other is SelectableItem<*> && item == other.item && isSelected == other.isSelected
}

data class FilterData(val period: Period = Period.All, val types: Set<EventData.Type> = setOf(*EventData.Type.values())) {
    enum class Period { All, Last_20, Last_15, Last_10, Last_5, Week, Month, Month_2, Month_3, Month_6 }
}