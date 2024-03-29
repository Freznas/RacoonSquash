package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

// Superklass
abstract class Ball(
    val context: Context,
    var ballPositionX: Float,
    var ballPositionY: Float,
    var ballSize: Float,
    var ballSpeedX: Float,
    var ballSpeedY: Float,
    color: Int
) {
    var paint = Paint()

    init {
        paint.color = color
    }

    abstract fun checkBounds(bounds: Rect)
    abstract fun draw(canvas: Canvas?)

    fun update() {

        ballPositionX += ballSpeedX
        ballPositionY += ballSpeedY

    }

}
