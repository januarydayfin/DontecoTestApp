package com.krayapp.dontecotestapp

import android.media.MediaPlayer
import com.krayapp.dontecotestapp.view.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Koin {
    fun getModules() = module {
        single { MediaPlayer() }

        viewModel<MainViewModel> { (MainViewModel()) }
    }
}