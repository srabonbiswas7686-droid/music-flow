package com.example.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import com.example.data.model.PlaybackState
import com.example.data.model.RepeatMode
import com.example.data.model.Song
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MusicPlayerController(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val onSongPlayed: (Song) -> Unit = {}
) {
    private val tag = "MusicPlayerController"
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var originalQueue: List<Song> = emptyList()

    private fun initMediaPlayer() {
        releasePlayer()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnPreparedListener { mp ->
                _playbackState.update {
                    it.copy(
                        isPlaying = true,
                        durationMs = mp.duration.toLong().coerceAtLeast(it.currentSong?.durationMs ?: 0L),
                        isBuffering = false,
                        errorMessage = null
                    )
                }
                mp.start()
                startProgressTicker()
            }
            setOnCompletionListener {
                handleSongCompletion()
            }
            setOnErrorListener { _, what, extra ->
                Log.e(tag, "MediaPlayer error: what=$what extra=$extra")
                _playbackState.update {
                    it.copy(
                        isPlaying = false,
                        isBuffering = false,
                        errorMessage = "Audio playback stream error ($what). Skipping or try again."
                    )
                }
                // Try skipping to next if error
                coroutineScope.launch {
                    delay(1500)
                    playNext()
                }
                true
            }
        }
    }

    fun playQueue(songs: List<Song>, startIndex: Int = 0) {
        if (songs.isEmpty()) return
        originalQueue = songs
        val queueToPlay = if (_playbackState.value.isShuffle) songs.shuffled() else songs
        val validIndex = startIndex.coerceIn(0, queueToPlay.lastIndex)
        _playbackState.update {
            it.copy(
                queue = queueToPlay,
                queueIndex = validIndex
            )
        }
        playSong(queueToPlay[validIndex])
    }

    fun playSong(song: Song) {
        coroutineScope.launch(Dispatchers.Main) {
            try {
                initMediaPlayer()
                _playbackState.update {
                    val currentQ = if (it.queue.isEmpty()) listOf(song) else it.queue
                    val currentIdx = currentQ.indexOfFirst { s -> s.id == song.id }.let { idx ->
                        if (idx >= 0) idx else 0
                    }
                    it.copy(
                        currentSong = song,
                        isPlaying = false,
                        isBuffering = true,
                        currentPositionMs = 0L,
                        durationMs = song.durationMs,
                        queue = currentQ,
                        queueIndex = currentIdx,
                        errorMessage = null
                    )
                }

                onSongPlayed(song)

                mediaPlayer?.apply {
                    reset()
                    setDataSource(song.audioUrl)
                    setVolume(_playbackState.value.volume, _playbackState.value.volume)
                    prepareAsync()
                }
            } catch (e: Exception) {
                Log.e(tag, "Failed to play song: ${e.message}", e)
                _playbackState.update {
                    it.copy(
                        isBuffering = false,
                        isPlaying = false,
                        errorMessage = "Unable to load stream: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer
        if (mp != null) {
            if (_playbackState.value.isPlaying) {
                mp.pause()
                stopProgressTicker()
                _playbackState.update { it.copy(isPlaying = false) }
            } else {
                mp.start()
                startProgressTicker()
                _playbackState.update { it.copy(isPlaying = true) }
            }
        } else {
            // If there's a current song or queue, play it
            val state = _playbackState.value
            val songToPlay = state.currentSong ?: state.queue.firstOrNull()
            if (songToPlay != null) {
                playSong(songToPlay)
            }
        }
    }

    fun seekTo(positionMs: Long) {
        val mp = mediaPlayer
        val targetMs = positionMs.coerceIn(0, _playbackState.value.durationMs)
        _playbackState.update { it.copy(currentPositionMs = targetMs) }
        mp?.seekTo(targetMs.toInt())
    }

    fun playNext() {
        val state = _playbackState.value
        if (state.queue.isEmpty()) return

        val nextIndex = state.queueIndex + 1
        if (nextIndex < state.queue.size) {
            _playbackState.update { it.copy(queueIndex = nextIndex) }
            playSong(state.queue[nextIndex])
        } else if (state.repeatMode == RepeatMode.ALL) {
            _playbackState.update { it.copy(queueIndex = 0) }
            playSong(state.queue[0])
        } else {
            // End of queue
            stopProgressTicker()
            _playbackState.update { it.copy(isPlaying = false, currentPositionMs = 0L) }
        }
    }

    fun playPrevious() {
        val state = _playbackState.value
        if (state.queue.isEmpty()) return

        // If played more than 3 seconds, restart current track
        if (state.currentPositionMs > 3000) {
            seekTo(0L)
            return
        }

        val prevIndex = state.queueIndex - 1
        if (prevIndex >= 0) {
            _playbackState.update { it.copy(queueIndex = prevIndex) }
            playSong(state.queue[prevIndex])
        } else {
            seekTo(0L)
        }
    }

    fun toggleShuffle() {
        val newShuffle = !_playbackState.value.isShuffle
        val currentSong = _playbackState.value.currentSong
        val newQueue = if (newShuffle) {
            val list = originalQueue.toMutableList()
            if (currentSong != null) {
                list.remove(currentSong)
                list.shuffle()
                list.add(0, currentSong)
                list
            } else {
                list.shuffled()
            }
        } else {
            originalQueue
        }
        val newIndex = currentSong?.let { newQueue.indexOf(it) } ?: 0
        _playbackState.update {
            it.copy(
                isShuffle = newShuffle,
                queue = newQueue,
                queueIndex = newIndex.coerceAtLeast(0)
            )
        }
    }

    fun toggleRepeat() {
        val nextMode = when (_playbackState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _playbackState.update { it.copy(repeatMode = nextMode) }
    }

    fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        _playbackState.update { it.copy(volume = clamped) }
        mediaPlayer?.setVolume(clamped, clamped)
    }

    fun addToQueue(song: Song) {
        val currentQueue = _playbackState.value.queue.toMutableList()
        currentQueue.add(song)
        _playbackState.update { it.copy(queue = currentQueue) }
        if (originalQueue.isNotEmpty()) {
            originalQueue = originalQueue + song
        }
    }

    fun removeFromQueue(index: Int) {
        val state = _playbackState.value
        if (index in state.queue.indices) {
            val currentQueue = state.queue.toMutableList()
            currentQueue.removeAt(index)
            val newIndex = when {
                index < state.queueIndex -> state.queueIndex - 1
                index == state.queueIndex -> state.queueIndex.coerceAtMost(currentQueue.lastIndex)
                else -> state.queueIndex
            }
            _playbackState.update { it.copy(queue = currentQueue, queueIndex = newIndex) }
        }
    }

    private fun handleSongCompletion() {
        when (_playbackState.value.repeatMode) {
            RepeatMode.ONE -> {
                seekTo(0L)
                mediaPlayer?.start()
                startProgressTicker()
            }
            RepeatMode.ALL -> {
                playNext()
            }
            RepeatMode.OFF -> {
                if (_playbackState.value.queueIndex < _playbackState.value.queue.lastIndex) {
                    playNext()
                } else {
                    stopProgressTicker()
                    _playbackState.update { it.copy(isPlaying = false, currentPositionMs = 0L) }
                }
            }
        }
    }

    private fun startProgressTicker() {
        stopProgressTicker()
        progressJob = coroutineScope.launch(Dispatchers.Main) {
            while (isActive) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        val pos = mp.currentPosition.toLong()
                        val dur = mp.duration.toLong().coerceAtLeast(_playbackState.value.durationMs)
                        _playbackState.update {
                            it.copy(
                                currentPositionMs = pos,
                                durationMs = dur
                            )
                        }
                    }
                }
                delay(400)
            }
        }
    }

    private fun stopProgressTicker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun releasePlayer() {
        stopProgressTicker()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e(tag, "Error releasing player", e)
        }
        mediaPlayer = null
    }
}
