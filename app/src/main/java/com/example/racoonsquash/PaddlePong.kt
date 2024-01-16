package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class PaddlePong(
    private val context: Context,
    var padPositionX: Float,
    var padPositionY: Float,
    val width: Float,
    val height: Float,
    color: Int
) {
    private var paint: Paint = Paint().apply {
        this.color = color
        style = Paint.Style.FILL
    }

    private var paddleRect: RectF = RectF(
        this.padPositionX - width / 2, this.padPositionY - height / 2,
        this.padPositionX + width / 2, this.padPositionY + height / 2
    )

    fun draw(canvas: Canvas) {
        canvas.drawRect(paddleRect, paint)
    }

    fun move(newX: Float) {
        padPositionX = newX
        keepPaddleInBounds()
        updatePaddleRect()
    }

    private fun keepPaddleInBounds() {
        val screenWidth = context.resources.displayMetrics.widthPixels
        padPositionX = padPositionX.coerceIn(width / 2, screenWidth - width / 2)
    }

    private fun updatePaddleRect() {
        paddleRect.set(
            padPositionX - width / 2, padPositionY - height / 2,
            padPositionX + width / 2, padPositionY + height / 2
        )
    }

    fun update() {

    }

    fun setPaddleTransparency(paddlePong: PaddlePong, alphaPaint: Int) {
        paddlePong.paint.alpha = alphaPaint

    }

}
