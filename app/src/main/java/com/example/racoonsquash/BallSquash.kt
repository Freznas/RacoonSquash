package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect

open class BallSquash(
    context: Context,
    ballSquashPositionX: Float,
    ballSquashPositionY: Float,
    ballSquashSize: Float,
    ballSquashSpeedX: Float,
    ballSquashSpeedY: Float,
    ballSquashColor: Int,
    val ballSquashSpeed: Float
) : Ball(context, ballSquashPositionX, ballSquashPositionY, ballSquashSize, ballSquashSpeedX, ballSquashSpeedY, ballSquashColor) {
    private val soundEffect: SoundEffect = SoundEffect(context)

    init {
        paint.color = ballSquashColor
    }

    override fun checkBounds(bounds: Rect) {
        // Kolla vänster och höger vägg
        if (ballPositionX + ballSize > bounds.right) {
            ballSpeedX *= -1
            if (ballPositionX + ballSize > bounds.right) {
                ballPositionX = bounds.right - ballSize
            }
        }

        // Kolla övre och nedre vägg
        if (ballPositionY - ballSize < bounds.top || ballPositionY + ballSize > bounds.bottom) {
            ballSpeedY *= -1
            if (ballPositionY - ballSize < bounds.top) {
                ballPositionY = bounds.top + ballSize
            } else if (ballPositionY + ballSize > bounds.bottom) {
                ballPositionY = bounds.bottom - ballSize
            }
        }
    }

    override fun draw(canvas: Canvas?) {
        canvas?.drawCircle(ballPositionX, ballPositionY, ballSize, paint)
    }

}
