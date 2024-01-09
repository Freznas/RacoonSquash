package com.example.racoonsquash

import android.content.Context
import android.media.MediaPlayer

class BackgroundMusic (context: Context) : Sound {

    private var mediaPlayer: MediaPlayer? = null

    init {
        mediaPlayer = MediaPlayer.create(context, R.raw.background_music_start)

    }

    override fun play(id: Int) {
        mediaPlayer?.setVolume(1.0f, 1.0f)
        mediaPlayer?.start()
    }

    override fun stop() {
        mediaPlayer?.stop()
        releaseMediaPlayer()
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun pauseMedia() {
        mediaPlayer?.pause()
        mediaPlayer?.seekTo(0)
    }

    fun loopTrack(looping: Boolean) {
        mediaPlayer?.isLooping = looping
    }
}