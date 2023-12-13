package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.SurfaceHolder
import android.view.SurfaceView

class PongGameView (context: Context) : SurfaceView(context), SurfaceHolder.Callback, Runnable{

    private var lineColor: Paint
    private var leftBoundaryPath: Path? = null
    private var rightBoundaryPath: Path? = null
    private val blockList: MutableList<BreakoutBlock> = mutableListOf()
    private val xPositionList: MutableList<Float> = mutableListOf()
    private val yPositionList: MutableList<Float> = mutableListOf()

    var mHolder: SurfaceHolder? = holder

    init {
        if (mHolder != null) {
            mHolder?.addCallback(this)
        }

        lineColor = Paint().apply {
            color = Color.CYAN
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }
    }

    private fun rowBlockPlacement(xPosition: Float) {
        xPositionList.add(xPosition)
    }

    private fun columnBlockPlacement(yPosition: Float) {
        yPositionList.add(yPosition)
    }

    private fun addBlockInList(block: BreakoutBlock) {
        blockList.add(block)
    }

    // Adding blocks in list the specified coordinates
    private fun buildBreakoutBlocks() {
        val colors: List<Int> = listOf(Color.GREEN, Color.RED, Color.CYAN, Color.RED, Color.GREEN)
        var newColor = colors.indexOf(Color.GREEN)

        for (y in yPositionList) {
            for (x in xPositionList) {
                addBlockInList(BreakoutBlock(x, y, x+180f, y+50f, colors[newColor]))
            }
            newColor+=1
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        val centerY = height/2-25f
        val centerX = width/2-90f

        // Blocks x-coordinates (Column-placement)
        rowBlockPlacement(centerX-400f)
        rowBlockPlacement(centerX-200f)
        rowBlockPlacement(centerX)
        rowBlockPlacement(centerX+200f)
        rowBlockPlacement(centerX+400f)

        // Blocks y-coordinates (Row-placement)
        columnBlockPlacement(centerY-140f)
        columnBlockPlacement(centerY-70f)
        columnBlockPlacement(centerY)
        columnBlockPlacement(centerY+70f)
        columnBlockPlacement(centerY+140f)

        buildBreakoutBlocks()

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        leftBoundaryPath = createBoundaryPathLeft(width, height)
        rightBoundaryPath = createBoundaryPathRight(width, height)
        drawGameBounds(holder)

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    override fun run() {

    }

    fun drawGameBounds(holder: SurfaceHolder) {
        val canvas: Canvas? = holder.lockCanvas()

        canvas?.drawColor(Color.BLACK)

        rightBoundaryPath?.let {
            canvas?.drawPath(it, lineColor)
        }

        leftBoundaryPath?.let {
            canvas?.drawPath(it, lineColor)
        }
        // Draw all blocks in blockList
        for (block in blockList) {
            block.draw(canvas)
        }

        holder.unlockCanvasAndPost(canvas)
    }

    // Enbart spelplan med linje för syns skull, vänster sidolinje.
    private fun createBoundaryPathLeft(width: Int, height: Int): Path {
        val pathLeft = Path()

        pathLeft.moveTo(0f, 0f)
        pathLeft.lineTo(0f, height.toFloat())

        return pathLeft
    }

    // Enbart spelplan med linje för syns skull, höger sidolinje.
    private fun createBoundaryPathRight(width: Int, height: Int): Path {
        val pathRight = Path()

        pathRight.moveTo(width.toFloat(), 0f)
        pathRight.lineTo(width.toFloat(), height.toFloat())

        return pathRight
    }
}