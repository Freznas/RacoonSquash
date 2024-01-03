package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect


abstract class Ball(
    val context: Context,
    var posX: Float,
    var posY: Float,
    var size: Float,
    var speedX: Float,
    var speedY: Float,
    color: Int
) {
    var paint = Paint()

    init {
        paint.color = color
    }

    abstract fun checkBounds(bounds: Rect)
    abstract fun draw(canvas: Canvas?)

    fun update() {

        posX += speedX
        posY += speedY

    }

}
