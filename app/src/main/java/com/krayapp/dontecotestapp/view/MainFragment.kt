package com.krayapp.dontecotestapp.view

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.krayapp.dontecotestapp.MainViewModel
import com.krayapp.dontecotestapp.MusicOnOpenResult
import com.krayapp.dontecotestapp.R
import com.krayapp.dontecotestapp.databinding.MainFragmentBinding
import com.krayapp.dontecotestapp.toast
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
    private val viewModel: MainViewModel by viewModel()

    private val mediaPlayer: MediaPlayer by inject()

    private var mediaPlayerFileStatus = false

    private var baseJob: Job? = null
    private val baseScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var currentVolume: Float = 0f
    private var currentFadeDuration = 2000
    private var currentMusicList: MutableList<Uri?> = mutableListOf()

    private val musicResult = registerForActivityResult(MusicOnOpenResult()) { result ->
        if (result != null) {
            viewModel.addTrack(result)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.volumeFlow.onEach { volume -> setVolume(volume) }.launchIn(lifecycleScope)
        viewModel.musicFlow.onEach { music -> addMusicToPlayList(music) }.launchIn(lifecycleScope)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
    }

    private fun startMusicExplorerJob() {
        if(!mediaPlayerFileStatus) {
            baseJob?.cancel()
            baseJob = baseScope.launch {
                mediaPlayer.reset()
                musicResult.launch("")
            }
        }
    }

    private fun playMusic() {
        mediaPlayer.setDataSource(requireContext(), currentMusicList[0]!!)
        mediaPlayer.prepare()
    }

    private fun addMusicToPlayList(track: Uri?) {
        if (track != null && !mediaPlayerFileStatus) {
            currentMusicList.add(track)
            viewBinding.open1File.setCardBackgroundColor(resources.getColor(R.color.light_green))
            if (currentMusicList.size >= 2) {
                viewBinding.open2File.setCardBackgroundColor(resources.getColor(R.color.light_green))
                mediaPlayer.reset()
                playMusic()
                mediaPlayerFileStatus = true
            } else {
                toast("Нужно 2 файла")
            }
        }
    }

    private fun setVolume(volume: Float) {
        currentVolume = volume
        mediaPlayer.setVolume(volume, currentVolume)
    }

    private fun setVolumeOnPause(){
        currentVolume = 0f
        viewModel.setOVolume()
    }
    private fun initButtons() {
        with(viewBinding) {
            open1File.setOnClickListener {
                startMusicExplorerJob()
            }
            open2File.setOnClickListener {
                startMusicExplorerJob()
            }
            playButton.setOnClickListener {
                if (mediaPlayer.isPlaying) {
                    setVolumeOnPause()
                    mediaPlayer.pause()
                } else {
                    if (mediaPlayerFileStatus) {
                        mediaPlayer.start()
                        viewModel.startFade(currentFadeDuration, FADE_IN)
                    } else {
                        toast("Файлы не загружены")
                    }
                }
            }
            fadeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                    currentFadeDuration = progress * 1000
                    viewBinding.durationCount.text = progress.toString()
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }

            })
        }
    }
}
