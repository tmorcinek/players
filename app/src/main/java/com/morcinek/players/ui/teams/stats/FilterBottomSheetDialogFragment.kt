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
import com.morcinek.players.core.extensions.*
import com.morcinek.players.databinding.FragmentFilterBinding
import com.morcinek.players.databinding.VhCheckboxBinding
import com.morcinek.players.databinding.VhRadioButtonBinding
import java.util.*

class FilterBottomSheetDialogFragment : BindingDialogFragment<FragmentFilterBinding>(FragmentFilterBinding::inflate) {

    lateinit var filterData : MutableLiveData<FilterData>

    private val currentDate: Calendar get() = calendar().apply { resetTime() }

    private val periods = listOf(
        Period.All,
        Period.Number(5),Period.Number(10),Period.Number(20),Period.Number(30),
        Period.AfterDate("This year", currentDate.apply {
            set(Calendar.DAY_OF_MONTH, 0)
            set(Calendar.MONTH, 0)
        }),
        Period.AfterDate("Last month", currentDate.minusMonth(1)),
        Period.AfterDate("Last 2 months", currentDate.minusMonth(2)),
        Period.AfterDate("Last 3 months", currentDate.minusMonth(3)),
        Period.AfterDate("Last 6 months", currentDate.minusMonth(6))
    )

    override fun onBindingView(binding: FragmentFilterBinding) = binding.run {
        periodGroup.setup {
            adapter(com.morcinek.android.listAdapter(itemCallback<SelectableItem<Period>>(), VhRadioButtonBinding::inflate) {
                onBind { _, item ->
                    root.run {
                        text = item.key
                        isChecked = item.isSelected
                        setOnClickListener { filterData.updateValue { copy(period = item.item) } }
                    }
                }
                liveData(viewLifecycleOwner, filterData.map { data -> periods.map { SelectableItem(it, it == data.period) } })
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
}
sealed class Period(val name: String, val events: (List<EventData>) -> List<EventData>) {
    object All : Period("All", { it })
    class AfterDate(name: String, afterDate: Calendar) : Period(name, { it.filter { eventData -> eventData.getDate().after(afterDate) }})
    class Number(lastEvents: Int) : Period("Last $lastEvents", { it.take(lastEvents)})

    override fun toString() = name

    override fun equals(other: Any?) = other is Period && other.name == name
}
