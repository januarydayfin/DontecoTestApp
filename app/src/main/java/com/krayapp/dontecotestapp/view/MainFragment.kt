package com.krayapp.dontecotestapp.view

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
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


    private val viewBinding: MainFragmentBinding by viewBinding()
    private val viewModel: MainViewModel by viewModel()

    private val mediaPlayer: MediaPlayer by inject()

    private var mediaPlayerFileStatus = false

    private var baseJob: Job? = null
    private val baseScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var currentVolume: Float = 0f
    private var currentFadeDuration = 2000
    private var currentMusicList: MutableList<Uri?> = mutableListOf()

    /**
     * Получение результата из активити
      **/
    private val musicResult = registerForActivityResult(MusicOnOpenResult()) { result ->
        if (result != null) {
            viewModel.addTrack(result)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.volumeFlow.onEach { volume -> setFadeVolume(volume) }.launchIn(lifecycleScope)
        viewModel.musicFlow.onEach { music -> addMusicToPlayList(music) }.launchIn(lifecycleScope)
        mediaPlayer.setOnCompletionListener(playerCompletionListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
    }

    /**
     * Listener, который слушает, когда заканчивается трек
     **/
    private val playerCompletionListener = MediaPlayer.OnCompletionListener { player ->
        viewModel.setOVolume()
        player.trackInfo
        player.reset()
        player.setDataSource(requireContext(), currentMusicList[1]!!)
        player.prepare()
        currentMusicList.reverse() //сделано из за условий в 2 файла, можно добавить итератор
        player.start()
        viewModel.startFade(currentFadeDuration)
    }

    /**
     * Запрос файлов в бэк потоке
     **/
    private fun startMusicExplorerJob() {
        if (!mediaPlayerFileStatus) {
            baseJob?.cancel()
            baseJob = baseScope.launch {
                musicResult.launch("")
            }
        }
    }

    /**
     * Первый запуск музыки
     **/
    private fun prepareMusic() {
        mediaPlayer.setDataSource(requireContext(), currentMusicList[0]!!)
        mediaPlayer.prepare()
    }

    /**
     * Добавляет файлы к текущему плейлисту
     **/
    private fun addMusicToPlayList(track: Uri?) {
        if (track != null && !mediaPlayerFileStatus) {
            currentMusicList.add(track)
            if (currentMusicList.size >= 2) {
                mediaPlayer.reset()
                prepareMusic()
                mediaPlayerFileStatus = true
                toast("Файлы успешно загружены")
            }
        }
    }

    /**
     * сетит фейд исходя из данных во вью модели
     **/
    private fun setFadeVolume(volume: Float) {
        currentVolume = volume
        mediaPlayer.setVolume(volume, currentVolume)
    }

    /**
     * сброс состояния над кнопкой плей
     **/
    private fun resetCondition() {
        mediaPlayer.stop()
        currentMusicList = mutableListOf()
        mediaPlayerFileStatus = false
        viewModel.setOVolume()
        with(viewBinding) {
            open1File.setCardBackgroundColor(resources.getColor(R.color.white))
            open2File.setCardBackgroundColor(resources.getColor(R.color.white))
            firstText.text = "Open 1 File"
            secondText.text = "Open 2 File"
        }
        toast("Сброс состояния")
    }

    private fun initButtons() {
        with(viewBinding) {
            open1File.setOnClickListener {
                startMusicExplorerJob()
                viewBinding.open1File.setCardBackgroundColor(resources.getColor(R.color.light_green))
                viewBinding.firstText.text = "√"
            }
            open2File.setOnClickListener {
                startMusicExplorerJob()
                viewBinding.open2File.setCardBackgroundColor(resources.getColor(R.color.light_green))
                viewBinding.secondText.text = "√"
            }
            playButton.setOnClickListener {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                } else {
                    if (mediaPlayerFileStatus) {
                        mediaPlayer.start()
                        viewModel.startFade(currentFadeDuration)
                    } else {
                        toast("Файлы не загружены, загрузите 2 файла")
                    }
                }
            }
            resetButton.setOnClickListener {
                resetCondition()
            }
            fadeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                    currentFadeDuration = progress * 1000
                    viewBinding.durationCount.text = progress.toString()
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
        }
    }
}
