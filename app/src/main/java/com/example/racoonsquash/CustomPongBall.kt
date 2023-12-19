package com.example.racoonsquash

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas

class CustomPongBall (context: Context,
                      posX: Float,
                      posY: Float,
                      size: Float,
                      speedX: Float,
                      speedY: Float,
                      color: Int
) : BallPong(context, posX, posY, size, speedX, speedY, color) {

    private var bitmap: Bitmap
    init {

        val smallerSize = BitmapFactory.Options()
        smallerSize.inSampleSize = 18

        bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.custom_pongball_green, smallerSize)

    }

    override fun draw(canvas: Canvas?) {
        val centerX = posX - bitmap.width / 2
        val centerY = posY - bitmap.height / 2

        canvas?.drawBitmap(bitmap, centerX, centerY, null)

    }
}