package com.example.racoonsquash

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import com.example.racoonsquash.databinding.ActivityPongBinding

class PongActivity : AppCompatActivity() {

    lateinit var binding: ActivityPongBinding
    private lateinit var gameView: PongGameView

   // 1 av 6 private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameView = PongGameView(this, intent.getStringExtra("userName")!!)

        val frameLayoutContainer: FrameLayout = binding.root.findViewById(R.id.frame_sv)

        frameLayoutContainer.addView(gameView)

        val restartButton: ImageButton = binding.btnRestartPong

        val pauseButton: ImageButton = binding.btnPausePong
        val playButton: ImageButton = binding.btnPlayPong
        gameView.setupButtons(pauseButton, playButton)

        restartButton.setOnClickListener {
            restartActivity()
        }

        // 2 av 6 mediaPlayer = MediaPlayer.create(this, R.raw.pongbreakout3)
        // 3 av 6 mediaPlayer.isLooping = true
        // 4 av 6 mediaPlayer.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        gameView.stop()
        gameView.soundEffect.releaseResource()
        // 5 av 6 mediaPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        gameView.stop()
        gameView.soundEffect.releaseResource()
        // 6 av 6 mediaPlayer.pause()
    }
    private fun restartActivity() {
        gameView.restartGame()
    }
}