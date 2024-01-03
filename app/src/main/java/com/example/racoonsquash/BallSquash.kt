package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

class BallSquash(
    context: Context,
    posX: Float,
    posY: Float,
    size: Float,
    speedX: Float,
    speedY: Float,
    color: Int,
    var speed: Float
) : Ball(context, posX, posY, size, speedX, speedY, color) {

    init {
        paint.color = color
    }

    override fun checkBounds(bounds: Rect) {
        // Kolla vänster och höger vägg
        if (posX + size > bounds.right) {
            speedX *= -1
            if (posX + size > bounds.right) {
                posX = bounds.right - size
            }
        }

        // Kolla övre och nedre vägg
        if (posY - size < bounds.top || posY + size > bounds.bottom) {
            speedY *= -1
            if (posY - size < bounds.top) {
                posY = bounds.top + size
            } else if (posY + size > bounds.bottom) {
                posY = bounds.bottom - size
            }
        }
    }

    override fun draw(canvas: Canvas?) {
        canvas?.drawCircle(posX, posY, size, paint)
    }

}
