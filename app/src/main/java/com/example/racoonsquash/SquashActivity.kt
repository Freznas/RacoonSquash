package com.example.racoonsquash

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.racoonsquash.databinding.ActivitySquashBinding

class SquashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySquashBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySquashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val surfaceHolder = binding.surfaceView.holder
        val gameView = SquashGameView(this)
        val container = binding.root
        container.addView(gameView)

    }


}


