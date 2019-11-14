package com.morcinek.players.ui.funino.creator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.morcinek.players.R
import com.morcinek.players.core.ContentFragmentManager
import com.morcinek.players.ui.teams.TeamsFragment

class FuninoCreatorActivity : AppCompatActivity() {

    val contentFragmentManager by lazy { ContentFragmentManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        contentFragmentManager.replaceFragment(TeamsFragment(), true)
    }
}

val Fragment.contentFragmentManager: ContentFragmentManager
    get() = (this as FuninoCreatorActivity).contentFragmentManager