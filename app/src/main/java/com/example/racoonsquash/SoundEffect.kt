package com.example.racoonsquash

import android.content.Context
import android.media.SoundPool

class SoundEffect(val context: Context) : Sound {

    // Soundpool-objekt med max antal ljud som kan spelas samtidigt
    private var soundPool: SoundPool? = SoundPool.Builder().setMaxStreams(10).build()

    private var audioID: Int = 0

    override fun play(id: Int) {
        this.audioID = id
        soundPool?.play(audioID, 1.0f, 1.0f, 2, 0, 1.0f)
    }

    override fun releaseResource() {
        soundPool?.release()
        soundPool = null
    }

    override fun stop() {
        soundPool?.stop(audioID)
    }

    fun reBuild() {
        if (soundPool == null) {
            soundPool = SoundPool.Builder().setMaxStreams(10).build()
        }
    }

    private fun getResource(resource: Int): Int {
        return when (resource) {
            0 -> R.raw.paddlebounce
            1 -> R.raw.wallbounce
            2 -> R.raw.lose
            3 -> R.raw.up
            4 -> R.raw.win
            5 -> R.raw.lost
            6 -> R.raw.squashbounce // 0
            7 -> R.raw.squashbouncy // 1
            8 -> R.raw.squashfailure //2
            9 -> R.raw.squashsuccessfanfare //3
            else -> 0
        }
    }

    fun loadPongSoundEffects(pongSoundList: MutableList<Int>) {
        for (i in 0..5) {
            val load = soundPool?.load(context, getResource(i), 1)
            if (load != null) {
                pongSoundList.add(load)
            }
        }
    }

    fun loadSquashSoundEffects(squashSoundList: MutableList<Int>) {
        for (i in 6..9) {
            val load = soundPool?.load(context, getResource(i), 1)
            if (load != null) {
                squashSoundList.add(load)
            }
        }
    }

    fun loadSoundEffect(load: Int) {
        soundPool?.load(context, getResource(load), 1)
    }
}