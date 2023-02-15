package com.morcinek.players.ui.teams

import android.os.Bundle
import android.view.View
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.android.HasKey
import com.morcinek.players.databinding.FragmentPlayerStatsBinding

class TeamStatsFragment : BaseFragment<FragmentPlayerStatsBinding>(FragmentPlayerStatsBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            title.setText(R.string.calendar_stats)
//            recyclerView.list<PlayerAttendance>()
        }
    }
}

private class PlayerAttendance(val name: String, val attendance: Attendance) : HasKey {
    override var key: String
        get() = name
        set(value) {}
}

private class Attendance(val text: String, val color: Int)