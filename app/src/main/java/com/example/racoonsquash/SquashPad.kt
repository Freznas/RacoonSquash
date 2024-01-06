package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable

class SquashPad(

    context: Context,
    padPosX: Float,
    posY: Float,
    size: Float,
    speedX: Float,
    speedY: Float,
    color: Int,
    val width: Float,
    val height: Float,
    speed: Float
) : BallSquash(context, padPosX, posY, size, speedX, speedY, color, speed) {


    var left: Float = padPosX - size
        private set

    var right: Float = padPosX + size
        private set

    var top: Float = posY - size
        private set

    var bottom: Float = posY + size
        private set
    var drawable: Drawable? = null

    init {
        drawable = context.resources.getDrawable(R.drawable.player_pad_a, null)

    }

    override fun draw(canvas: Canvas?) {
        drawable?.let {
            it.setBounds(
                (ballPositionX - it.intrinsicWidth / 2).toInt(),
                (ballPositionY - it.intrinsicHeight / 2).toInt(),
                (ballPositionX + it.intrinsicWidth / 2).toInt(),
                (ballPositionY + it.intrinsicHeight / 2).toInt()
            )
            canvas?.let { it1 -> it.draw(it1) }
        }
    }
}