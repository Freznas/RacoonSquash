package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Typeface
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random
import android.view.MotionEvent

class PongGameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback, Runnable {
    var thread: Thread? = null
    var running = false
    var lineColor: Paint
    private var leftBoundaryPath: Path? = null
    private var rightBoundaryPath: Path? = null
    var touchColor: Paint
    var scorePaint: Paint

    var isGameOver = false
    var isGameWon = false
    lateinit var gameWonPaint: Paint
    private var textGameOverPaint: Paint

    //    private var scorePlayerTop = 0
//    private var scorePlayerBottom = 0
    private val blockList: MutableList<BreakoutBlock> = mutableListOf()
    private val xPositionList: MutableList<Float> = mutableListOf()
    private val yPositionList: MutableList<Float> = mutableListOf()
    lateinit var ballPong: BallPong
    var bounds = Rect()
    var mHolder: SurfaceHolder? = holder
//    private val initialBallPosX = 500f
//    private val initialBallPosY = 700f

    private var score = 0
    private lateinit var paddle: PaddlePong
    private lateinit var topPaddle: PaddlePong
    private val initialBallPosXForTop = 500f
    private val initialBallPosYForTop = 1300f
    private val initialBallPosXForBottom = 300f
    private val initialBallPosYForBottom = 500f
    private var lives = 3 // Antal liv


    private val soundEffect = SoundEffect(context)

    init {
        if (mHolder != null) {
            mHolder?.addCallback(this)
        }

        lineColor = Paint().apply {
            color = Color.CYAN
            style = Paint.Style.STROKE
            strokeWidth = 10f

            scorePaint = Paint().apply {
                color = Color.GREEN
                alpha = 200
                textSize = 60.0F
                typeface = Typeface.create("serif-monospace", Typeface.BOLD)
            }
            textGameOverPaint = Paint().apply {
                color = Color.RED
                alpha = 200
                textSize = 60.0F
                typeface = Typeface.create("serif-monospace", Typeface.BOLD)
            }
            // Enbart för att synliggöra gränserna
            lineColor = Paint().apply {
                color = Color.MAGENTA
                style = Paint.Style.STROKE
                strokeWidth = 10f
            }
            touchColor = Paint().apply {
                color = Color.RED
                style = Paint.Style.STROKE
                strokeWidth = 50f
            }
        }
        setup()
    }

    private val screenWidth = resources.displayMetrics.widthPixels
    private val screenHeight = resources.displayMetrics.heightPixels

    private fun setup() {
      
        ballPong = BallPong(context, 100f, 100f, 30f, 15f, 15f, 0)

        paddle = PaddlePong(
            context,
            screenWidth / 2f,
            screenHeight - 100f,  // for bottom paddle
            180f,
            20f,
            Color.parseColor("#FFFF00")
        )
        topPaddle = PaddlePong(
            context,
            screenWidth / 2f,
            50f,  // for top paddle
            180f,
            20f,
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

                performClick()
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        // Call the super implementation to handle the click event
        super.performClick()
        // Return true if the click event is handled
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


    private fun loseLife() {
        lives--
        if(lives<=0){
            isGameOver =true
        }
    }


    fun update() {
        checkWinCondition()
        ballPong.update()
        checkBallBlockCollision()
        paddle.update()
        topPaddle.update()
        val screenHeight = height // Höjden på skärmen
        if (lives <= 0) {
            stop()
        }
        // Check collision with the bottom paddle
        if (isBallCollidingWithPaddle(ballPong, paddle)) {
            soundEffect.play(0)
            ballPong.speedY = -ballPong.speedY // Reverse Y-direction
        }

        // Check collision with the top paddle
        if (isBallCollidingWithPaddle(ballPong, topPaddle)) {
            soundEffect.play(0)
            ballPong.speedY = -ballPong.speedY // Reverse Y-direction
        }


        if (ballPong.posY < -ballPong.size) {

            loseLife()


            soundEffect.play(2)

            resetBallPosition()

        } else if (ballPong.posY > screenHeight + ballPong.size) {

            loseLife()
            resetBallPosition()


        } else if (ballPong.posX < 0) {
//            scorePlayerBottom = 0
//            scorePlayerTop = 0

            score = 0

        }
        if (checkWinCondition() == true) {
            stop()
        }

//        detta behöver vi om vi ska ha en maxpoäng (ändra bara 11an i if satsen till önskat maxpoäng)
//        if (score >= 11) {
//
//            try {
//                Thread.sleep(5000)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//            score = 0
//            scorePlayerBottom = 0
//            scorePlayerTop = 0
//    }
        resetBallPosition()
    }


    private fun checkWinCondition(): Boolean {
        return blockList.isEmpty()
    }

    private fun resetBallPosition() {
        // Placera bollen på olika startpositioner beroende på var den åker ut

        if (ballPong.posY < -ballPong.size) {
            Thread.sleep(0)
            ballPong.posX = initialBallPosXForTop
            ballPong.posY = initialBallPosYForTop
        } else if (ballPong.posY > screenHeight - ballPong.size) {
            Thread.sleep(0)
            ballPong.posX = initialBallPosXForBottom
            ballPong.posY = initialBallPosYForBottom
        }
    }


    override fun run() {
        while (running) {
            update()
            drawGameBounds(holder)
            ballPong.checkBounds(bounds)

        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        val blockWidth = 180f
        val blockHeight = 50f
        val centerX = (width / 2) - (blockWidth / 2)
        val centerY = (height / 2) - (blockHeight / 2)
        setup()

        // Column positions
        columnBlockPosition(centerX - 400f)
        columnBlockPosition(centerX - 200f)
        columnBlockPosition(centerX)
        columnBlockPosition(centerX + 200f)
        columnBlockPosition(centerX + 400f)

        // Row positions
        rowBlockPosition(centerY - 140f)
        rowBlockPosition(centerY - 70f)
        rowBlockPosition(centerY)
        rowBlockPosition(centerY + 70f)
        rowBlockPosition(centerY + 140f)

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

    private fun columnBlockPosition(xPosition: Float) {
        xPositionList.add(xPosition)
    }

    private fun rowBlockPosition(yPosition: Float) {
        yPositionList.add(yPosition)
    }

    private fun deleteBlockInList(block: BreakoutBlock) {
        blockList.remove(block)
    }

    private fun addBlockInList(block: BreakoutBlock) {
        blockList.add(block)
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

    private fun checkBallBlockCollision() {
        for (block in blockList) {
            if (onBlockCollision(block, ballPong)) {
                ballPong.speedY *= -1

                score++

                soundEffect.play(3)

                deleteBlockInList(block)
                break
            }
        }
    }


    private fun onBlockCollision(block: BreakoutBlock, ball: BallPong): Boolean {
        // CommonX sparar bollens x-position om den befinner sig inom blockets x-position
        val commonX = if (ball.posX < block.posX) {
            block.posX
        } else if (ball.posX > block.width) {
            block.width
        } else {
            ball.posX
        }

        // CommonY sparar bollens y-position om den befinner sig inom blockets y-position
        val commonY = if (ball.posY < block.posY) {
            block.posY
        } else if (ball.posY > block.height) {
            block.height
        } else {
            ball.posY
        }

        // Här räknas distansen ut. Exempel, om bollens x-position är 50 och commonX
        // är samma, dvs. 50 så blir x-distansens 0. Samma gäller för Y.
        val distance =
            sqrt(
                (ball.posX - commonX).toDouble().pow(2.0) + (ball.posY - commonY).toDouble()
                    .pow(2.0)
            )

        // Returnerar true när distansen är 0 och drar bort bollens size.
        return distance < ball.size

    }

    fun drawGameBounds(holder: SurfaceHolder) {
        val canvas: Canvas? = holder.lockCanvas()

        canvas?.drawColor(Color.BLACK)

        val livesText = "Lives: $lives"
        canvas?.drawText(livesText, 20f, 100f, scorePaint)

        rightBoundaryPath?.let {
            if (checkWinCondition())
                canvas?.drawText(
                    "winText",
                    canvas.width.toFloat() / 3,
                    canvas.height.toFloat() - 300,
                    gameWonPaint
                )


            if (isGameOver)
                canvas?.drawText(
                    "GAME OVER",
                    canvas.width.toFloat() / 3,
                    canvas.height.toFloat() - 300,
                    textGameOverPaint
                )

            canvas?.drawPath(it, lineColor)
            if (ballPong.posY < 0 - ballPong.size) {
                canvas?.drawPath(it, touchColor)
                canvas?.drawText(
                    "Score: $score",
                    canvas.width.toFloat() - 400,
                    0f + 100,
                    textGameOverPaint
                )


            } else {
                // Placera text
                canvas?.drawText(
                    "Score: $score",
                    canvas.width.toFloat() - 400,
                    0f + 100,
                    scorePaint
                )
            }
            if (lives <= 0)
                canvas?.drawText(
                    "GAME OVER",
                    canvas.width.toFloat() / 3,
                    canvas.height.toFloat() - 300,
                    textGameOverPaint
                )
            if(isGameWon)
                canvas?.drawText("CONGRATZ",canvas.width.toFloat() / 3,
                    canvas.height.toFloat() - 300,gameWonPaint)
        }

        leftBoundaryPath?.let {
            canvas?.drawPath(it, lineColor)
        }

        // Draw the paddles
        paddle.draw(canvas!!)
        topPaddle.draw(canvas)

        //Draw all blocks
        for (block in blockList) {
            block.draw(canvas)
        }


        ballPong.draw(canvas)
        holder.unlockCanvasAndPost(canvas)
    }

    private fun isBallCollidingWithPaddle(ball: BallPong, paddle: PaddlePong): Boolean {
        // Check if the ball is within the horizontal bounds of the paddle
        val horizontalCollision = ball.posX + ball.size > paddle.positionX - paddle.width / 2 &&
                ball.posX - ball.size < paddle.positionX + paddle.width / 2

        // Check if the ball is within the vertical bounds of the paddle
        val verticalCollision = ball.posY + ball.size > paddle.positionY - paddle.height / 2 &&
                ball.posY - ball.size < paddle.positionY + paddle.height / 2

        return horizontalCollision && verticalCollision
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
//    private fun updateScore(): Int {
//        score++
//        return score
//    }
//    private fun updateScoreTop(): Int {
//        scorePlayerTop++
//        return scorePlayerTop
//    }
//
//    private fun updateScoreBottom(): Int {
//        scorePlayerBottom++
//        return scorePlayerBottom
//    }
}