package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class PaddlePong(
    private val context: Context,
    initialX: Float,
    initialY: Float,
    private val width: Float,
    private val height: Float,
    color: Int
) {
    private val paint: Paint = Paint().apply {
        this.color = color
        style = Paint.Style.FILL
    }

    var positionX: Float = initialX
    var positionY: Float = initialY

    private var paddleRect: RectF = RectF(
        positionX - width / 2, positionY - height / 2,
        positionX + width / 2, positionY + height / 2
    )

    fun draw(canvas: Canvas) {
        canvas.drawRect(paddleRect, paint)
    }

    fun move(newX: Float) {
        positionX = newX
        keepPaddleInBounds()
        updatePaddleRect()
    }

    private fun keepPaddleInBounds() {
        val screenWidth = context.resources.displayMetrics.widthPixels
        positionX = positionX.coerceIn(width / 2, screenWidth - width / 2)
    }

    private fun updatePaddleRect() {
        paddleRect.set(
            positionX - width / 2, positionY - height / 2,
            positionX + width / 2, positionY + height / 2
        )
    }

    fun update() {

    }

}
