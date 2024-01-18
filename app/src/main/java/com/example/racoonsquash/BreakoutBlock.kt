package com.example.racoonsquash

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
// mall för blocken
class BreakoutBlock(
    context: Context,
    val posX: Float, // Left position
    val posY: Float, // Top position
    val width: Float, // Right position
    val height: Float, // Bottom position
    private val bitmapNumber: Int
) {

    private val bitmap: List<Bitmap>

    init {

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