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
    private val soundEffect =  SoundEffect(context)
    var paint = Paint()

    init {
        paint.color = color
    }

    fun checkBounds(bounds: Rect) {
        // Kolla vänster och höger vägg
        if (posX - size < bounds.left || posX + size > bounds.right) {
            soundEffect.play(1)
            speedX *= -1
            if (posX - size < bounds.left) {
                posX = bounds.left + size
            } else if (posX + size > bounds.right) {
                posX = bounds.right - size
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
