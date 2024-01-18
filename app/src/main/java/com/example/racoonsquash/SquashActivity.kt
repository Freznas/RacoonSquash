package com.example.racoonsquash

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.racoonsquash.databinding.ActivitySquashBinding

class SquashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySquashBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var gameView: SquashGameView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySquashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameView = SquashGameView(this, intent.getStringExtra("userName")!!)
        val container = binding.root
        container.addView(gameView)


        // Skapa en MediaPlayer-instans för att spela din MP3-fil
        mediaPlayer = MediaPlayer.create(this, R.raw.squashbackgroundmusic)
        mediaPlayer?.isLooping = true
    }

    override fun onResume() {
        super.onResume()
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start() // Starta musiken när aktiviteten är synlig
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause() // Pausa musiken när användaren lämnar aktiviteten
        }
        gameView.soundEffect.releaseResource()
        gameView.thread?.interrupt()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release() // Frigör resurser när aktiviteten förstörs, som mediaplayern anvander
        gameView.soundEffect.releaseResource()
        gameView.thread?.interrupt()

    }
}


