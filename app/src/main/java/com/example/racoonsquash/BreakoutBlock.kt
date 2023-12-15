package com.example.racoonsquash

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint

class BreakoutBlock(
    context: Context,
    private val posX: Float, // Left position
    private val posY: Float, // Top position
    private val width: Float, // Right position
    private val height: Float, // Bottom position
    private val bitmapNumber: Int
) {
    private var paint = Paint()
    private val bitmap: List<Bitmap>

    init {

        //paint.color = color

        bitmap = listOf(
            BitmapFactory.decodeResource(context.resources, R.drawable.block_pink),
            BitmapFactory.decodeResource(context.resources, R.drawable.block_blue),
            BitmapFactory.decodeResource(context.resources, R.drawable.block_green)
        )
    }

    fun draw(canvas: Canvas?) {

//        val stroke = Paint().apply {
//            color = Color.WHITE
//            style = Paint.Style.STROKE
//            strokeWidth = 4f
//        }
//
//        canvas?.drawRect(posX, posY, width, height, paint)
//        canvas?.drawRect(posX, posY, width, height, stroke)

        when (bitmapNumber) {
            0 -> canvas?.drawBitmap(bitmap[0], posX, posY, null)
            1 -> canvas?.drawBitmap(bitmap[1], posX, posY, null)
            2 -> canvas?.drawBitmap(bitmap[2], posX, posY, null)
        }
    }
}