package com.example.racoonsquash

import android.content.Context
import android.media.SoundPool

class SoundEffect (context: Context) : Sound {

    // Soundpool-objekt med max antal ljud som kan spelas samtidigt
    private val soundPool: SoundPool = SoundPool.Builder().setMaxStreams(10).build()

    private val bouncePaddleSound: Int = soundPool.load(context, R.raw.paddlebounce, 0)
    private val wallBounceSound: Int = soundPool.load(context, R.raw.wallbounce, 1)
    private val gameOverSound: Int = soundPool.load(context, R.raw.lose, 2)
    private val upSound: Int = soundPool.load(context, R.raw.up, 1)

    // Sätter id på varje ljud och beroende på id, spela upp ljudeffekt
    private var audioID: Int = 0

    override fun play(id: Int) {

        this.audioID = id

        when(id) {
            0 -> soundPool.play(bouncePaddleSound, 1.0f, 1.0f, 0, 0, 1.0f)
            1 -> soundPool.play(wallBounceSound, 1.0f, 1.0f, 1, 0, 1.0f)
            2 -> soundPool.play(gameOverSound, 1.0f, 1.0f, 1, 0, 1.0f)
            3 -> soundPool.play(upSound, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    override fun stop() {
        soundPool.stop(audioID)
    }
}