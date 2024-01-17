package com.example.racoonsquash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import com.example.racoonsquash.databinding.ActivityPongBinding
import android.media.MediaPlayer
import android.util.Log


class PongActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPongBinding
    private lateinit var gameView: PongGameView
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPongBinding.inflate(layoutInflater)
        setContentView(binding.root)
       mediaPlayer= MediaPlayer.create(this, R.raw.pongbreakout3)

        mediaPlayer.start()

        mediaPlayer.isLooping = true

        gameView = PongGameView(this, intent.getStringExtra("userName")!!)

        //        val container = binding.root
        //        container.addView(gameView)

        val frameLayoutContainer: FrameLayout = binding.root.findViewById(R.id.frame_sv)

        frameLayoutContainer.addView(gameView)

        val restartButton: ImageButton = binding.btnRestartPong

        val pauseButton: ImageButton = binding.btnPausePong
        val playButton: ImageButton = binding.btnPlayPong
        gameView.setupButtons(pauseButton, playButton)

        restartButton.setOnClickListener {

            restartActivity()
        }

    }

    override fun onResume() {
        super.onResume()
        if (mediaPlayer.isPlaying)
            mediaPlayer.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        gameView.stop()
        gameView.soundEffect.releaseResource()
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
        gameView.thread?.interrupt()
        gameView.soundEffect.releaseResource()

    }

    private fun restartActivity() {
        gameView.restartGame()

    }
}