package com.example.racoonsquash

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class BreakoutBlock(
    private val posX: Float, // Left position
    private val posY: Float, // Top position
    private val width: Float, // Right position
    private val height: Float, // Bottom position
    color: Int
) {
    private var paint = Paint()

    init {
        paint.color = color
    }

    fun draw(canvas: Canvas?) {

        val stroke = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }

        canvas?.drawRect(posX, posY, width, height, paint)
        canvas?.drawRect(posX, posY, width, height, stroke)
    }
}