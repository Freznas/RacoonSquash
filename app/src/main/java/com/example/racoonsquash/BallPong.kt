package com.example.racoonsquash

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect

class BallPong(
    context: Context,
    posX: Float,
    posY: Float,
    size: Float,
    speedX: Float,
    speedY: Float,
    color: Int
) : Ball(context, posX, posY, size, speedX, speedY, color) {

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

    override fun draw(canvas: Canvas?) {
        val centerX = posX - bitmap.width / 2
        val centerY = posY - bitmap.height / 2

        canvas?.drawBitmap(bitmap, centerX, centerY, null)

    }
}