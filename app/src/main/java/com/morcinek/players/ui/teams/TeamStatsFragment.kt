package com.morcinek.players.ui.teams

import android.os.Bundle
import android.view.View
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.HasKey
import kotlinx.android.synthetic.main.fragment_player_stats.*

class TeamStatsFragment : BaseFragment(R.layout.fragment_player_stats) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.run {
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