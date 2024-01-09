package com.example.racoonsquash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.racoonsquash.databinding.ActivityPongBinding

class PongActivity : AppCompatActivity() {

    lateinit var binding: ActivityPongBinding
    lateinit var gameView: PongGameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameView = PongGameView(this, intent.getStringExtra("userName")!!)
        val container = binding.root
        container.addView(gameView)

        val pauseButton: ImageButton = binding.btnPausePong
        val playButton: ImageButton = binding.btnPlayPong
        gameView.setupButton(pauseButton, playButton)

    }

    override fun onDestroy() {
        super.onDestroy()
        gameView.stop()
    }

    override fun onPause() {
        super.onPause()
        gameView.stop()
    }
}