package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class PaddlePong(
    private val context: Context,
    var padPongPositionX: Float,
    var padPongPositionY: Float,
    val padPongWidth: Float,
    val padPongHeight: Float,
    padPongColor: Int
) {
    private val paint: Paint = Paint().apply {
        this.color = padPongColor
        style = Paint.Style.FILL
    }

    private var paddleRect: RectF = RectF(
        this.padPongPositionX - padPongWidth / 2, this.padPongPositionY - padPongHeight / 2,
        this.padPongPositionX + padPongWidth / 2, this.padPongPositionY + padPongHeight / 2
    )

    fun draw(canvas: Canvas) {
        canvas.drawRect(paddleRect, paint)
    }

    fun move(newX: Float) {
        padPongPositionX = newX
        keepPaddleInBounds()
        updatePaddleRect()
    }

    private fun keepPaddleInBounds() {
        val screenWidth = context.resources.displayMetrics.widthPixels
        padPongPositionX = padPongPositionX.coerceIn(padPongWidth / 2, screenWidth - padPongWidth / 2)
    }

    private fun updatePaddleRect() {
        paddleRect.set(
            padPongPositionX - padPongWidth / 2, padPongPositionY - padPongHeight / 2,
            padPongPositionX + padPongWidth / 2, padPongPositionY + padPongHeight / 2
        )
    }

    fun update() {

    }

}
