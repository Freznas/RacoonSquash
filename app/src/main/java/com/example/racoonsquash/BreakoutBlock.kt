package com.example.racoonsquash

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log

class BreakoutBlock(
    context: Context,
    var posX: Float, // Left position
    var posY: Float, // Top position
    var width: Float, // Right position
    var height: Float, // Bottom position
    private val bitmapNumber: Int

) {
    var isDestroyed: Boolean = false

    private val bitmap: List<Bitmap>

    init {
        Log.d("BreakoutBlock", "posX: $posX, posY: $posY, width: $width, height: $height")

        bitmap = listOf(
            BitmapFactory.decodeResource(context.resources, R.drawable.block_pink),
            BitmapFactory.decodeResource(context.resources, R.drawable.block_blue),
            BitmapFactory.decodeResource(context.resources, R.drawable.block_green))

    }

    fun draw(canvas: Canvas?) {

        when (bitmapNumber) {
            0 -> canvas?.drawBitmap(bitmap[0], posX, posY, null)
            1 -> canvas?.drawBitmap(bitmap[1], posX, posY, null)
            2 -> canvas?.drawBitmap(bitmap[2], posX, posY, null)
        }
    }
}