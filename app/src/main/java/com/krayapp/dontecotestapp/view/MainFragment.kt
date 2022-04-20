package com.krayapp.dontecotestapp.view

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.krayapp.dontecotestapp.MainViewModel
import com.krayapp.dontecotestapp.MusicOnOpenResult
import com.krayapp.dontecotestapp.R
import com.krayapp.dontecotestapp.databinding.MainFragmentBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment(R.layout.main_fragment) {
    companion object {
        fun newInstance(): Fragment = MainFragment()
    }
    private val FADE_IN = true
    private val FADE_OUT = false

    private val viewBinding: MainFragmentBinding by viewBinding()
    private val viewModel:MainViewModel by viewModel()

    private val mediaPlayer: MediaPlayer by inject()

    private var mediaPlayerFileStatus = false

    private var baseJob: Job? = null
    private val baseScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var currentVolume:Float = 0f

    private val musicResult = registerForActivityResult(MusicOnOpenResult()) { result ->
        if (result != null) {
            mediaPlayer.setDataSource(requireContext(),result)
            mediaPlayer.prepare()
            mediaPlayerFileStatus = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.volumeFlow.onEach { volume ->
            currentVolume = volume
            mediaPlayer.setVolume(volume,currentVolume)
            println("VVV $volume")
        }.launchIn(lifecycleScope)
        /*viewModel.liveData.observe(viewLifecycleOwner){
            volume ->
            currentVolume = volume
            mediaPlayer.setVolume(currentVolume,volume)
        }*/
    }
    private fun initButtons() {
        with(viewBinding) {
            open1File.setOnClickListener {
                baseJob?.cancel()
                baseJob = baseScope.launch {
                    mediaPlayer.reset()
                    musicResult.launch("")
                }
            }
            playButton.setOnClickListener {
                if(mediaPlayer.isPlaying){
                    mediaPlayer.pause()
                }else{
                    if(mediaPlayerFileStatus) {
                        mediaPlayer.start()
                        viewModel.startFade(2000, FADE_IN)
                    }else{
                        Toast.makeText(context,"Файлы не загружены", Toast.LENGTH_SHORT ).show()
                    }
                }
            }
        }
    }
}
