package com.krayapp.dontecotestapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class MainViewModel() : ViewModel() {
    private val _volumeFlow: MutableStateFlow<Float> = MutableStateFlow(0f)
    val volumeFlow: StateFlow<Float> = _volumeFlow.asStateFlow()

    private val _liveData = MutableLiveData<Float>()
    val liveData: LiveData<Float>
        get() = _liveData

    private val FADE_INTERVAL = 250
    private var volume: Float = 0f

    fun startFade(fadeDuration: Int, fadeIn: Boolean) {
        val delta = fadeDuration / FADE_INTERVAL.toFloat() // количество ступеней снижения/повышения
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
    }

    private fun fadeStep(delta: Float, fadeIn: Boolean) {
        _volumeFlow.value = volume // равняет громкость к измененной громкости
        if (fadeIn) {
            volume += delta
            _volumeFlow.value = volume
        } else {
            volume -= delta
            _volumeFlow.value = volume
        }
    }
}