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
import com.example.racoonsquash.databinding.ActivityPongBinding
import com.example.racoonsquash.databinding.ActivitySquashBinding

class PongActivity : AppCompatActivity() {

    lateinit var binding: ActivityPongBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gameView = PongGameView(this)
        val container = binding.root
        container.addView(gameView)

    }

}