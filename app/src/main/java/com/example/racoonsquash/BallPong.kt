package com.example.racoonsquash

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect

class BallPong(
    context: Context,
    ballPongPositionX: Float,
    ballPongPositionY: Float,
    ballPongSize: Float,
    ballPongSpeedX: Float,
    ballPongSpeedY: Float,
    ballPongColor: Int
) : Ball(context, ballPongPositionX, ballPongPositionY, ballPongSize, ballPongSpeedX, ballPongSpeedY, ballPongColor) {

    private var bitmap: Bitmap
    private val soundEffect: SoundEffect = SoundEffect(context)

    init {

        val smallerSize = BitmapFactory.Options()
        smallerSize.inSampleSize = 14

        bitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.custom_pongball_green,
            smallerSize
        )

    }

    override fun checkBounds(bounds: Rect) {
        // Kolla vänster och höger vägg
        if (ballPositionX - ballSize < bounds.left || ballPositionX + ballSize > bounds.right) {
            soundEffect.play(1)
            ballSpeedX *= -1
            if (ballPositionX - ballSize < bounds.left) {
                ballPositionX = bounds.left + ballSize
            } else if (ballPositionX + ballSize > bounds.right) {
                ballPositionX = bounds.right - ballSize
            }
        }

    }

    override fun draw(canvas: Canvas?) {
        val centerX = ballPositionX - bitmap.width / 2
        val centerY = ballPositionY - bitmap.height / 2

        canvas?.drawBitmap(bitmap, centerX, centerY, null)

    }
}