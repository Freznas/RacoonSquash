package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable

class SquashPad(

    context: Context,
    val padPosX: Float,
    val padPosY: Float,
    size: Float,
    speedX: Float,
    speedY: Float,
    color: Int,
    val width: Float,
    val height: Float,
    speed: Float
) : BallSquash(context, padPosX, padPosY, size, speedX, speedY, color) {

// Parametrar för startposition av squashPad i SquashGameView
    private val initialPosX = padPosX
    private val initialPosY = padPosY
    private val initialWidth = width
    private val initialHeight = height


    var left: Float = padPosX - size
        private set

    var right: Float = padPosX + size
        private set

    var top: Float = padPosY - size
        private set

    var bottom: Float = padPosY + size
        private set
    var drawable: Drawable? = null

    init {
        drawable = context.resources.getDrawable(R.drawable.player_pad_a, null)

    }

    override fun draw(canvas: Canvas?) {
        drawable?.let {
            it.setBounds(
                (padPosX - it.intrinsicWidth / 2).toInt(),
                (ballPositionY - it.intrinsicHeight / 2).toInt(),
                (padPosX + it.intrinsicWidth / 2).toInt(),
                (ballPositionY + it.intrinsicHeight / 2).toInt()
            )
            canvas?.let { it1 -> it.draw(it1) }
        }
    }

    override fun reset() {
        super.reset()

        ballPositionX = initialPosX
        ballPositionY = initialPosY

        drawable?.let {
            it.setBounds(
                (initialPosX - it.intrinsicWidth / 2).toInt(),
                (padPosY - it.intrinsicHeight / 2).toInt(),
                (initialPosX + it.intrinsicWidth / 2).toInt(),
                (padPosY + it.intrinsicHeight / 2).toInt()
            )
        }

        left = initialPosX - initialWidth / 2
        right = initialPosX + initialWidth / 2
        top = initialPosY - initialHeight / 2
        bottom = initialPosY + initialHeight / 2
    }
}