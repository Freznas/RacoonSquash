package com.example.racoonsquash

import android.content.Context
import android.media.SoundPool

class SoundEffect (context: Context) : Sound {

    // Soundpool-objekt med max antal ljud som kan spelas samtidigt
    private val soundPool: SoundPool = SoundPool.Builder().setMaxStreams(5).build()

    private val bouncePaddleSound: Int = soundPool.load(context, R.raw.paddlebounce, 2)
    private val wallBounceSound: Int = soundPool.load(context, R.raw.wallbounce, 2)
    private val gameOverSound: Int = soundPool.load(context, R.raw.lose, 1)
    private val upSound: Int = soundPool.load(context, R.raw.up, 2)
    private val squashHitPaddleSound: Int = soundPool.load(context, R.raw.squashbounce, 2)
    private val squashSuccessSound: Int = soundPool.load(context, R.raw.squashsuccess, 1)
    private val squashFailureSound: Int = soundPool.load(context, R.raw.squashfailure, 1)
    private val squashHitWallSound: Int = soundPool.load(context, R.raw.squashhitwall, 2)

    // Sätter id på varje ljud och beroende på id, spela upp ljudeffekt
    private var audioID: Int = 0

    override fun play(id: Int) {

        this.audioID = id

        when(id) {
            0 -> soundPool.play(bouncePaddleSound, 1.0f, 1.0f, 2, 0, 1.0f)
            1 -> soundPool.play(wallBounceSound, 1.0f, 1.0f, 2, 0, 1.0f)
            2 -> soundPool.play(gameOverSound, 1.0f, 1.0f, 1, 0, 1.0f)
            3 -> soundPool.play(upSound, 1.0f, 1.0f, 2, 0, 1.0f)
            4 -> soundPool.play(squashHitPaddleSound, 1.0f, 1.0f,1, 0, 1.0f)
            5 -> soundPool.play(squashSuccessSound, 1.0f, 1.0f, 1, 0, 1.0f)
            6 -> soundPool.play(squashFailureSound, 1.0f, 1.0f, 1, 0, 1.0f)
            7 -> soundPool.play(squashHitWallSound, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    override fun stop() {
        soundPool.stop(audioID)
    }
}