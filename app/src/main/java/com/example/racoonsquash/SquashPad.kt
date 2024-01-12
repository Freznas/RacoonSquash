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

    // Initial state
    private val initialPosX = padPosX
    private val initialPosY = posY
    private val initialWidth = width
    private val initialHeight = height


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

    override fun reset() {
        super.reset()

        // Reset the position of the paddle
        ballPositionX = initialPosX
        ballPositionY = initialPosY

        // Reset the drawable's bounds based on the initial position
        drawable?.let {
            it.setBounds(
                (initialPosX - it.intrinsicWidth / 2).toInt(),
                (initialPosY - it.intrinsicHeight / 2).toInt(),
                (initialPosX + it.intrinsicWidth / 2).toInt(),
                (initialPosY + it.intrinsicHeight / 2).toInt()
            )
        }

        // Reset the boundaries of the pad
        left = initialPosX - initialWidth / 2
        right = initialPosX + initialWidth / 2
        top = initialPosY - initialHeight / 2
        bottom = initialPosY + initialHeight / 2
    }



}