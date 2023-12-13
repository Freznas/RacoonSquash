package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect


open class BallPong(
    context: Context,
    var posX: Float,
    var posY: Float,
    var size: Float,
    var speedX: Float,
    var speedY: Float,
    color: Int,

    ) {
    var paint = Paint()

    init {
        paint.color = color
    }

    fun checkBounds(bounds: Rect) {
        // Kolla vänster och höger vägg
        if (posX - size < bounds.left || posX + size > bounds.right) {
            speedX *= -1
            if (posX - size < bounds.left) {
                posX = bounds.left + size
            } else if (posX + size > bounds.right) {
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

    open fun update() {

        posX += speedX
        posY += speedY

    }

    open fun draw(canvas: Canvas?) {
        canvas?.drawCircle(posX, posY, size, paint)
    }

}
