package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.ImageButton
import androidx.core.view.isVisible
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random


class PongGameView(context: Context, private val userName: String) : SurfaceView(context),
    SurfaceHolder.Callback, Runnable {
    var thread: Thread? = null
    var running = false
    var lineColor: Paint
    private var leftBoundaryPath: Path? = null
    private var rightBoundaryPath: Path? = null
    var lineColorGameOver: Paint
    var scorePaint: Paint
    var isGameOver = false
    var isGameWon = false
    var isGameReset = false
    var textGameWonPaint: Paint
    private var textGameOverPaint: Paint
    private val blockList: MutableList<BreakoutBlock> = mutableListOf()
    private val xPositionList: MutableList<Float> = mutableListOf()
    private val yPositionList: MutableList<Float> = mutableListOf()
    lateinit var ballPong: BallPong
    var bounds = Rect()
    var mHolder: SurfaceHolder? = holder
    private lateinit var pauseButton: ImageButton
    private var score = 0
    private lateinit var paddle: PaddlePong
    private lateinit var topPaddle: PaddlePong
    private val initialBallPosXForTop = 500f
    private val initialBallPosYForTop = 1300f
    private val initialBallPosXForBottom = 300f
    private val initialBallPosYForBottom = 500f
    private var lives = 3
    private var isPaused = false
    private val bounceSpeedXFactor = 10.0f //Kontrollerar vinkel på studs vid kollision med padel
    private var soundEffectsList: MutableList<Int> = mutableListOf()
    val soundEffect = SoundEffect(context)

    init {
        if (mHolder != null) {
            mHolder?.addCallback(this)
        }

        soundEffect.loadPongSoundEffects(soundEffectsList)
//Ritar upp texter med färg och typsnitt
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
            textGameWonPaint = Paint().apply {
                color = Color.GREEN
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

            lineColorGameOver = Paint().apply {
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
        isGameReset = false

        ballPong = BallPong(context, 150f, 150f, 30f, 15f, 15f, 0)

        paddle = PaddlePong(
            context,
            screenWidth / 2f,
            screenHeight - 300f,
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
        isGameWon = false
    }


    fun setupButtons(pauseButton: ImageButton, playButton: ImageButton) {
//hanterar bytet av play & pause knapp baserat på klickläge
        pauseButton.setOnClickListener {
            this.pauseButton = pauseButton
            if (!isGameWon && lives > 0) {
                isPaused = true
                playButton.isVisible = true
                pauseButton.isVisible = false
            }
        }
        playButton.setOnClickListener {
            if (!isGameWon && lives > 0) {
                isPaused = false
                playButton.isVisible = false
                pauseButton.isVisible = true
            }
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
//Styr båda paddlarna samtidigt med fingret
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val newX = event.x
                paddle.move(newX)
                topPaddle.move(newX)

                performClick()
            }
        }
        return true
    }

    //Hanterar kontrol av paddlar (se onTouchEvent ↑)
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun restartGame() {
        stop()
        soundEffect.stop()
        soundEffect.releaseResource()
        soundEffectsList.clear()

//Vid omstart ställer om poäng & liv till rätt värden
        isGameReset = true
        score = 0
        lives = 3

        setup()

        if (isPaused) {
            isPaused = false
            pauseButton.isVisible = true
        }

        synchronized(blockList) {
            blockList.clear()
            buildBreakoutBlocks()
        }
        start()
    }


    fun start() {
        running = true
        soundEffect.reBuild()
        soundEffect.loadPongSoundEffects(soundEffectsList)

        thread = Thread(this)

        thread?.start()

    }

    fun stop() {
        running = false


        try {
            thread?.interrupt() //join betyder att huvudtraden komemr vanta in att tråden dor ut av sig sjalv
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }


    private fun loseLife() {
        lives--
        if (lives <= 0) {
            soundEffect.play(soundEffectsList[2])
            isGameOver = true
        } else {
            soundEffect.play(soundEffectsList[5])
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

//Kontrollera kollision med botten padeln
        if (isBallCollidingWithPaddle(ballPong, paddle)) {
            soundEffect.play(soundEffectsList[0])
            handleBallPaddleCollision(ballPong, paddle)
        }

//Kontrollera kollision med top padeln
        if (isBallCollidingWithPaddle(ballPong, topPaddle)) {
            soundEffect.play(soundEffectsList[0])
            handleBallPaddleCollision(ballPong, topPaddle)
        }

        if (ballPong.ballPositionY < -ballPong.ballSize) {

            resetBallPosition()

        } else if (ballPong.ballPositionY > screenHeight + ballPong.ballSize) {

            resetBallPosition()

        } else if (ballPong.ballPositionX < 0) {

            score = 0

        }
        if (checkWinCondition() == true) {
            isGameWon = true
            soundEffect.play(soundEffectsList[4])

        }


        resetBallPosition()
    }


    private fun checkWinCondition(): Boolean {

        return blockList.isEmpty()
    }

    private fun resetBallPosition() {
// Placera bollen på olika startpositioner beroende på var den åker ut och ta bort ett liv

        if (ballPong.ballPositionY < -ballPong.ballSize) {
            loseLife()
            ballPong.ballPositionX = initialBallPosXForTop
            ballPong.ballPositionY = initialBallPosYForTop
        } else if (ballPong.ballPositionY > screenHeight - ballPong.ballSize) {
            loseLife()
            ballPong.ballPositionX = initialBallPosXForBottom
            ballPong.ballPositionY = initialBallPosYForBottom
        }
    }

    private fun setTransparentPaddle(paddle: PaddlePong) {
        when (lives) {
            2 -> paddle.setPaddleTransparency(paddle, 80)
            1 -> paddle.setPaddleTransparency(paddle, 40)
            else -> paddle.setPaddleTransparency(paddle, 255)
        }
    }

    override fun run() {
        while (running) {
            if (!isPaused) {
                update()
                setTransparentPaddle(paddle)
                setTransparentPaddle(topPaddle)
                drawGamePong(holder)
                ballPong.checkBounds(bounds)
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
//Hanterar positioner för BreakoutBlock
        val blockWidth = 175f
        val blockHeight = 50f
        val centerX = (width / 2) - (blockWidth / 2)
        val centerY = (height / 2) - (blockHeight / 2)
        setup()
// Kolumn positioner
        columnBlockPosition(centerX - 400f)
        columnBlockPosition(centerX - 200f)
        columnBlockPosition(centerX)
        columnBlockPosition(centerX + 200f)
        columnBlockPosition(centerX + 400f)

// Rad positioner
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

    private fun deleteBlockInList(block: BreakoutBlock): Boolean {

        blockList.remove(block)
        return blockList.isEmpty()
    }

    private fun addBlockInList(block: BreakoutBlock) {
        blockList.add(block)
    }



//Lägger till block i listan i rader och kolumner
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
        synchronized(blockList) {
            for (block in blockList) {
                if (onBlockCollision(block, ballPong)) {
                    ballPong.ballSpeedY *= -1

                    score++

                    soundEffect.play(soundEffectsList[3])

                    isGameWon = deleteBlockInList(block)

                    break
                }
            }

        }


    }


    private fun onBlockCollision(block: BreakoutBlock, ball: BallPong): Boolean {
        // CommonX sparar bollens x-position om den befinner sig inom blockets x-position
        val commonX = if (ball.ballPositionX < block.posX) {
            block.posX
        } else if (ball.ballPositionX > block.width) {
            block.width
        } else {
            ball.ballPositionX
        }

        // CommonY sparar bollens y-position om den befinner sig inom blockets y-position
        val commonY = if (ball.ballPositionY < block.posY) {
            block.posY
        } else if (ball.ballPositionY > block.height) {
            block.height
        } else {
            ball.ballPositionY
        }

        // Här räknas distansen ut. Exempel, om bollens x-position är 50 och commonX
        // är samma, dvs. 50 så blir x-distansens 0. Samma gäller för Y.
        val distance =
            sqrt(
                (ball.ballPositionX - commonX).toDouble()
                    .pow(2.0) + (ball.ballPositionY - commonY).toDouble()
                    .pow(2.0)
            )

        // Returnerar true när distansen är 0 och drar bort bollens size.
        return distance < ball.ballSize

    }

    fun drawGamePong(holder: SurfaceHolder) {
        val canvas: Canvas? = holder.lockCanvas()

        canvas?.drawColor(Color.BLACK)

        val livesText = "Lives: $lives"
        canvas?.drawText(livesText, 20f, 100f, scorePaint)

        rightBoundaryPath?.let {
            if (isGameWon && !isGameReset) {
                canvas?.drawText(
                    "CONGRATZ",
                    canvas.width.toFloat() / 3,
                    canvas.height.toFloat() - 300,
                    textGameWonPaint
                )
// Spara poäng vid vinst
                val sharedPreferencesManager: DataManager = SharedPreferencesManager(context)
                sharedPreferencesManager.addNewScore(
                    DataManager.Score(
                        this.userName,
                        score,
                        DataManager.Game.BREAKOUT
                    )
                )

            }

            if (isGameOver && isGameReset) {
                canvas?.drawText(
                    "GAME OVER",
                    canvas.width.toFloat() / 3,
                    canvas.height.toFloat() - 300,
                    textGameOverPaint
                )

            }
            canvas?.drawPath(it, lineColor)
            if (ballPong.ballPositionY < 0 - ballPong.ballSize) {
                canvas?.drawPath(it, lineColorGameOver)
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
            if (lives <= 0) {
                canvas?.drawText(
                    "GAME OVER",
                    canvas.width.toFloat() / 3,
                    canvas.height.toFloat() - 300,
                    textGameOverPaint
                )

//Spara poäng vid gameover
                val sharedPreferencesManager: DataManager = SharedPreferencesManager(context)
                sharedPreferencesManager.addNewScore(
                    DataManager.Score(
                        this.userName,
                        score,
                        DataManager.Game.BREAKOUT
                    )
                )
            }
        }

        leftBoundaryPath?.let {
            canvas?.drawPath(it, lineColor)
        }


//Rita upp padlarna
        paddle.draw(canvas!!)
        topPaddle.draw(canvas)

        synchronized(blockList) {
            for (block in blockList) {
                block.draw(canvas)
            }
        }


        ballPong.draw(canvas)
        holder.unlockCanvasAndPost(canvas)

        if (isGameWon) {
            stop()

        }
    }

    private fun isBallCollidingWithPaddle(ball: BallPong, paddle: PaddlePong): Boolean {
//Kontrolle om bollen är inom horizontella gränserna av padeln
        val horizontalCollision =
            ball.ballPositionX + ball.ballSize > paddle.padPositionX - paddle.width / 2 &&
                    ball.ballPositionX - ball.ballSize < paddle.padPositionX + paddle.width / 2

//Kontrolle om bollen är inom vertikala gränserna av padeln

        val verticalCollision =
            ball.ballPositionY + ball.ballSize > paddle.padPositionY - paddle.height / 2 &&
                    ball.ballPositionY - ball.ballSize < paddle.padPositionY + paddle.height / 2

        return horizontalCollision && verticalCollision
    }

    private fun handleBallPaddleCollision(ball: BallPong, paddle: PaddlePong) {
// Invertera Y-hastigheten för att bollen ska studsa
        ball.ballSpeedY = -ball.ballSpeedY

// Justera X-hastigheten baserat på träffpunkten på paddeln
        val impactPoint = (ball.ballPositionX - paddle.padPositionX) / (paddle.width / 2)

        ball.ballSpeedX = bounceSpeedXFactor * impactPoint
    }

//Enbart spelplan med linje för syns skull, vänster sidolinje.
    private fun createBoundaryPathLeft(width: Int, height: Int): Path {
        val pathLeft = Path()

        pathLeft.moveTo(0f, 0f)
        pathLeft.lineTo(0f, height.toFloat())

        return pathLeft
    }

//Enbart spelplan med linje för syns skull, höger sidolinje.
    private fun createBoundaryPathRight(width: Int, height: Int): Path {
        val pathRight = Path()

        pathRight.moveTo(width.toFloat(), 0f)
        pathRight.lineTo(width.toFloat(), height.toFloat())

        return pathRight
    }

}