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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mediaPlayer: MediaPlayer= MediaPlayer.create(this, R.raw.pongbreakout3)

        mediaPlayer.start()
        mediaPlayer.setOnErrorListener { mp, what, extra ->
            Log.e("MediaPlayer", "Error: $what, Extra: $extra")
            false
        }
        mediaPlayer.isLooping=true

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

    override fun onDestroy() {
        super.onDestroy()
        gameView.stop()
        gameView.soundEffect.releaseResource()

    }

    override fun onPause() {
        super.onPause()
        gameView.stop()
        gameView.soundEffect.releaseResource()

    }

    private fun restartActivity() {
        gameView.restartGame()
    }

}