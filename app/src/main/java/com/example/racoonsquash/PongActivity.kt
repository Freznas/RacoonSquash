package com.example.racoonsquash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.racoonsquash.databinding.ActivityPongBinding

class PongActivity : AppCompatActivity() {

    lateinit var binding: ActivityPongBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val surfaceHolder = binding.svPong.holder
        val gameView = PongGameView(this)
        val container = binding.root
        container.addView(gameView)


        val pauseButton: ImageButton = binding.btnPausePong
        val playButton: ImageButton = binding.btnPlayPong
        gameView.setupButton(pauseButton, playButton)


    }

}