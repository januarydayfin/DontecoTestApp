package com.krayapp.dontecotestapp.view

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.krayapp.dontecotestapp.MusicOnOpenResult
import com.krayapp.dontecotestapp.R
import com.krayapp.dontecotestapp.databinding.MainFragmentBinding
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class MainFragment : Fragment(R.layout.main_fragment) {
    companion object {
        fun newInstance(): Fragment = MainFragment()
    }

    private val viewBinding: MainFragmentBinding by viewBinding()
    private val mediaPlayer: MediaPlayer by inject()
    private var playlist = mutableListOf<Uri?>()
    private var baseJob: Job? = null
    private val baseScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val musicResult = registerForActivityResult(MusicOnOpenResult()) { result ->
        if (result != null) {
            mediaPlayer.setDataSource(requireContext(),result)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
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
                mediaPlayer.prepare()
                if(mediaPlayer.isPlaying){
                    mediaPlayer.pause()
                }else{
                    mediaPlayer.start()
                }
            }
        }
    }
}
