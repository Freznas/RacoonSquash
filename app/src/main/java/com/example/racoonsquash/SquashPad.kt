package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable

class SquashPad(

    context: Context,
    padSquashPositionX: Float,
    padSquashPositionY: Float,
    padSquashSize: Float,
    padSquashSpeedX: Float,
    padSquashSpeedY: Float,
    padSquashColor: Int,
    val padSquashWidth: Float,
    val padSquashHeight: Float,
    padSquashSpeed: Float
) : BallSquash(context, padSquashPositionX, padSquashPositionY, padSquashSize, padSquashSpeedX, padSquashSpeedY, padSquashColor, padSquashSpeed) {


    var left: Float = padSquashPositionX - padSquashSize
        private set

    var right: Float = padSquashPositionX + padSquashSize
        private set

    var top: Float = padSquashPositionY - padSquashSize
        private set

    var bottom: Float = padSquashPositionY + padSquashSize
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