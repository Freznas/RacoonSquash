package com.example.racoonsquash

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.ImageButton
import com.example.racoonsquash.databinding.ActivityPongBinding
import com.example.racoonsquash.databinding.ActivitySquashBinding

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

        val pauseButton: ImageButton = findViewById(R.id.btn_play_pause_pong)
        gameView.setupButton(pauseButton)

    }

}