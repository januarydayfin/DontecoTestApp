package com.krayapp.dontecotestapp

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class MainViewModel : ViewModel() {
    private val _volumeFlow: MutableStateFlow<Float> = MutableStateFlow(0f)
    val volumeFlow: StateFlow<Float> = _volumeFlow.asStateFlow()

    private val _musicFlow: MutableStateFlow<Uri?> = MutableStateFlow(null)
    val musicFlow: StateFlow<Uri?> = _musicFlow.asStateFlow()

    private val MAX_VOLUME = 1f
    private val FADE_INTERVAL = 50L
    private var volume: Float = 0f

    fun addTrack(track: Uri?) {
        _musicFlow.value = track
    }

    fun startFade(fadeDuration: Int, fadeIn: Boolean) {
        val numberSteps = fadeDuration / FADE_INTERVAL.toFloat() //
        val delta = MAX_VOLUME / numberSteps
        val timer = Timer(true)
        val timerTask = object : TimerTask() {
            override fun run() {
                fadeStep(delta, fadeIn)
                if (fadeIn) {
                    if (volume >= 1f) { // если громкость становится большей или равной единице - таймер останавливается
                        timer.cancel()
                        timer.purge()
                    }
                } else {
                    if (volume <= 0f) {
                        timer.cancel()
                        timer.purge()
                    }
                }
            }
        }
        timer.schedule(timerTask, FADE_INTERVAL, FADE_INTERVAL)
    }

    private fun fadeStep(delta: Float, fadeIn: Boolean) {
        _volumeFlow.value = volume // равняет громкость к измененной громкости
        if (fadeIn) {
            volume += delta
        } else {
            volume -= delta
        }
        _volumeFlow.value = volume
    }
    fun setOVolume(){
        volume = 0f
    }
}