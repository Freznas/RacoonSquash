package com.example.racoonsquash

import android.content.Context
import android.media.SoundPool

class SoundEffect (context: Context) : Sound {

    // Soundpool-objekt med max antal ljud som kan spelas samtidigt
    private val soundPool: SoundPool = SoundPool.Builder().setMaxStreams(10).build()

    private val bouncePaddleSound: Int = soundPool.load(context, R.raw.paddlebounce, 1)
    private val wallBounceSound: Int = soundPool.load(context, R.raw.wallbounce, 1)
    private val gameOverSound: Int = soundPool.load(context, R.raw.lose, 1)
    private val upSound: Int = soundPool.load(context, R.raw.up, 1)
    private val winSound: Int = soundPool.load(context, R.raw.win, 1)
    private val lostLifeSound: Int = soundPool.load(context, R.raw.lost, 1)
    private val squashBounceSound: Int = soundPool.load(context, R.raw.squashbounce, 1)
    private val squashBouncySound: Int = soundPool.load(context, R.raw.squashbouncy, 2)
    private val squashFailureSound: Int = soundPool.load(context, R.raw.squashfailure, 2)

    // Sätter id på varje ljud och beroende på id, spela upp ljudeffekt
    private var audioID: Int = 0

    override fun play(id: Int) {

        this.audioID = id

        when(id) {
            0 -> soundPool.play(bouncePaddleSound, 1.0f, 1.0f, 2, 0, 1.0f)
            1 -> soundPool.play(wallBounceSound, 1.0f, 1.0f, 2, 0, 1.0f)
            2 -> soundPool.play(gameOverSound, 1.0f, 1.0f, 1, 0, 1.0f)
            3 -> soundPool.play(upSound, 1.0f, 1.0f, 2, 0, 1.0f)
            4 -> soundPool.play(squashBounceSound, 1.0f, 1.0f, 1, 0, 1.0f)
            5 -> soundPool.play(squashBouncySound, 1.0f, 1.0f, 1, 0, 1.0f)
            6 -> soundPool.play(squashFailureSound, 1.0f, 1.0f, 1, 0, 1.0f)
            7 -> soundPool.play(winSound, 1.0f, 1.0f, 2, 0, 1.0f)
            8 -> soundPool.play(lostLifeSound, 1.0f, 1.0f, 2, 0, 1.0f)
        }
    }

    override fun releaseResource() {
        soundPool.release()
    }

    override fun stop() {
        soundPool.stop(audioID)
    }
}