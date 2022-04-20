package com.krayapp.dontecotestapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.krayapp.dontecotestapp.view.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance())
            .commitAllowingStateLoss()
    }
}