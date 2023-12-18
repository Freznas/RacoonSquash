package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random
import android.view.MotionEvent

class PongGameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback, Runnable {
    var thread: Thread? = null
    var running = false
    private var lineColor: Paint
    private var leftBoundaryPath: Path? = null
    private var rightBoundaryPath: Path? = null

    private val blockList: MutableList<BreakoutBlock> = mutableListOf()
    private val xPositionList: MutableList<Float> = mutableListOf()
    private val yPositionList: MutableList<Float> = mutableListOf()

    val screenHeight: Int
        get() = this.displayMetrics.heightPixels
    val displayMetrics = resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    lateinit var ballPong: BallPong
    private lateinit var paddle: PaddlePong
    private lateinit var topPaddle: PaddlePong
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
        ballPong = BallPong(context, 100f, 100f, 15f, 20f, 20f, Color.RED, 10f)
        paddle = PaddlePong(
            context,
            screenWidth / 2f, // Initial X position
            screenHeight - 100f, // Initial Y position - bottom of the screen
            180f, // Width
            20f, // Height
            Color.parseColor("#FFFF00")
        )
        topPaddle = PaddlePong(
            context,
            screenWidth / 2f, // Initial X position
            50f, // Initial Y position - top of the screen
            180f, // Width
            20f, // Height
            Color.parseColor("#FFFF00")
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // Move both paddles in sync based on the touch input
                val newX = event.x
                paddle.move(newX)
                topPaddle.move(newX)
            }
        }
        return true
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
        paddle.update()
        topPaddle.update()
        checkBlockBallCollision()
    }

    override fun run() {
        while (running) {
            update()
            drawGameBounds(holder)
            ballPong.checkBounds(bounds)

        }
    }

    private fun columnBlockPlacement(xPosition: Float) {
        xPositionList.add(xPosition)
    }

    private fun rowBlockPlacement(yPosition: Float) {
        yPositionList.add(yPosition)

    }

    private fun addBlockInList(block: BreakoutBlock) {
        blockList.add(block)
    }

    private fun deleteBlockInList(block: BreakoutBlock) {
        blockList.remove(block)
    }

    // Adding blocks in list in rows and columns
    private fun buildBreakoutBlocks() {
        var randomBitmap = Random.nextInt(0, 3)
        val blockWidth = 175f
        val blockHeight = 50f

        for (y in yPositionList) {
            for (x in xPositionList) {
                addBlockInList(
                    BreakoutBlock(
                        this.context, x, y, x + blockWidth, y + blockHeight,
                        randomBitmap
                    )
                )
                randomBitmap = Random.nextInt(0, 3)
            }
        }
    }

    private fun onBlockCollision(block: BreakoutBlock, ball: BallPong): Boolean {
        val blockX = if (ball.posX < block.getPosX()) {
            block.getPosX()
        } else if (ball.posX > block.getWidth()) {
            block.getWidth()
        } else {
            ball.posX
        }

        val blockY = if (ball.posY < block.getPosY()) {
            block.getPosY()
        } else if (ball.posY > block.getHeight()) {
            block.getHeight()
        } else {
            ball.posY
        }

        val distance = sqrt((ball.posX - blockX).toDouble().pow(2.0) + (ball.posY - blockY).toDouble().pow(2.0))

        return distance < ball.size
    }



    private fun checkBlockBallCollision() {
        for (block in blockList) {
            if (onBlockCollision(block, ballPong)) {
                ballPong.speedY *= -1
                deleteBlockInList(block)
                break
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        val blockWidth = 180f
        val blockHeight = 50f
        val centerX = (width / 2) - (blockWidth / 2)
        val centerY = (height / 2) - (blockHeight / 2)

        // Blocks column-placement
        columnBlockPlacement(centerX - 400f)
        columnBlockPlacement(centerX - 200f)
        columnBlockPlacement(centerX)
        columnBlockPlacement(centerX + 200f)
        columnBlockPlacement(centerX + 400f)

        // Blocks row-placement
        rowBlockPlacement(centerY - 140f)
        rowBlockPlacement(centerY - 70f)
        rowBlockPlacement(centerY)
        rowBlockPlacement(centerY + 70f)
        rowBlockPlacement(centerY + 140f)

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

        // Draw the paddles
        paddle.draw(canvas!!)
        topPaddle.draw(canvas)

        // Draw all blocks
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


