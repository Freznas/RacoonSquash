package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.random.Random
import kotlin.random.nextInt

class PongGameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback, Runnable {
    var thread: Thread? = null
    var running = false
    private var lineColor: Paint
    private var leftBoundaryPath: Path? = null
    private var rightBoundaryPath: Path? = null

    private val blockList: MutableList<BreakoutBlock> = mutableListOf()
    private val xPositionList: MutableList<Float> = mutableListOf()
    private val yPositionList: MutableList<Float> = mutableListOf()

    lateinit var ballPong: BallPong
    var bounds = Rect()
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
        setup()
    }

    private fun setup() {
        ballPong = BallPong(context, 100f, 100f, 15f, 20f, 20f, Color.RED)

    }

    fun start() {
        running = true
        thread = Thread(this) //en trad har en konstruktor som tar in en runnable,
        // vilket sker i denna klass se rad 10
        thread?.start()

    }

    fun stop() {
        running = false
        thread?.join()
        try {
            thread?.join() //join betyder att huvudtraden komemr vanta in att traden dor ut av sig sjalv
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun update() {
        ballPong.update()

    }

    override fun run() {
        while (running) {
            update()
            drawGameBounds(holder)
            ballPong.checkBounds(bounds)

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

    // Adding blocks in list in rows and columns
    private fun buildBreakoutBlocks() {
        var randomBitmap = Random.nextInt(0, 3)
        val blockWidth = 180f
        val blockHeight = 50f

        for (y in yPositionList) {
            for (x in xPositionList) {
                addBlockInList(BreakoutBlock(this.context, x, y, x+blockWidth, y+blockHeight, randomBitmap))
                randomBitmap = Random.nextInt(0, 3)
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        val blockWidth = 180f
        val blockHeight = 50f
        val centerX = (width/2)-(blockWidth/2)
        val centerY = (height/2)-(blockHeight/2)

        // Blocks row-placement
        rowBlockPlacement(centerX-400f)
        rowBlockPlacement(centerX-200f)
        rowBlockPlacement(centerX)
        rowBlockPlacement(centerX+200f)
        rowBlockPlacement(centerX+400f)

        // Blocks column-placement
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
        bounds = Rect(0, 0, width, height)
        start()

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stop()
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

        //Draw all blocks
        for (block in blockList) {
            block.draw(canvas)
        }

        ballPong.draw(canvas)
        holder.unlockCanvasAndPost(canvas)
        this.setZOrderOnTop(true)
    }

    //     Enbart spelplan med linje för syns skull, vänster sidolinje.
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