package com.krayapp.dontecotestapp.view

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


    fun startFade(fadeDuration: Int) {
        val numberSteps = fadeDuration / FADE_INTERVAL.toFloat() //количество шагов фейда
        val delta = MAX_VOLUME / numberSteps //размер шага
        val timer = Timer(true)
        val timerTask = object : TimerTask() {
            override fun run() {
                fadeStep(delta)
                if (volume >= 1f) { // если громкость становится большей или равной единице - таймер останавливается
                    timer.cancel()
                    timer.purge()
                }
            }
        }
        timer.schedule(timerTask, FADE_INTERVAL, FADE_INTERVAL)
    }

    /**
     * сетит зачение измененной громкости во flow
     **/
    private fun fadeStep(delta: Float) {
            volume += delta
        _volumeFlow.value = volume
    }

    /**
     * сетит нулевое значение по громкости, при сбросе
     **/
    fun setOVolume() {
        volume = 0f
        _volumeFlow.value = volume
    }

    /**
     * добавляет трек в flow
     **/
    fun addTrack(track: Uri?) {
        _musicFlow.value = track
    }
}