package com.krayapp.dontecotestapp

import android.media.MediaPlayer
import org.koin.dsl.module

object Koin {
    fun getModules() = module {
        single { MediaPlayer() }
    }
}