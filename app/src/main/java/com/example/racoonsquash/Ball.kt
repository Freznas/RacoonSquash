package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect


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

    open fun update() {

        ballPositionX += ballSpeedX
        ballPositionY += ballSpeedY

    }
    
}
