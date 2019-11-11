package com.morcinek.players.ui.funino

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.morcinek.players.R
import kotlinx.android.synthetic.main.app_bar_main.*

class FuninoCreatorActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        setSupportActionBar(toolbar)
    }
}