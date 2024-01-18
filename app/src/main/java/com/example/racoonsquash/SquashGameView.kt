package com.example.racoonsquash

import android.annotation.SuppressLint
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


class SquashGameView(context: Context, private val userName: String) : SurfaceView(context),
    SurfaceHolder.Callback, Runnable {
    var thread: Thread? = null
    private var running = false
    lateinit var ballSquash: BallSquash
    lateinit var squashPad: SquashPad
    private var lineColor: Paint
    private var touchColor: Paint
    private var scorePaint: Paint
    private var textGameOverPaint: Paint
    private var score: Int = 0;
    private var isPaused = false
    private var soundEffectsList: MutableList<Int> = mutableListOf()
    val soundEffect = SoundEffect(context)
    private var gameBoundaryPath: Path? = null
    var bounds = Rect()
    var mHolder: SurfaceHolder? = holder
    private var gameOver = false
    private var buttonPauseRect: Rect? = null
    private val buttonPausePaint = Paint().apply {
        color = Color.YELLOW
        alpha = 100
    }
    private val buttonPauseTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 60.0F
        typeface = Typeface.create("serif-monospace", Typeface.BOLD)
    }
    private var buttonPauseText = context.getString(R.string.pauseText)
    private var buttonRestartRect: Rect? = null
    private val buttonRestartPaint = Paint().apply {
        color = Color.YELLOW
        alpha = 100
    }
    private val buttonRestartTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 60.0F
        typeface = Typeface.create("serif-monospace", Typeface.BOLD)
    }
    private var buttonRestartText = "Restart"

    init {
        if (mHolder != null) {
            mHolder?.addCallback(this)
        }

        soundEffect.loadSquashSoundEffects(soundEffectsList)

// Score-text-färg-attribut
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
        setup()
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setup() {

        ballSquash = BallSquash(this.context, 100f, 100f, 30f, 20f, 20f, Color.RED)


// Tar in paramterar från SquashPad.kt
        squashPad = SquashPad(
            this.context, 50f, 400f, 6f, 0f, 0f, 0,
            15f, 75f, 0f
        )
    }

    private fun checkGameOver(): Boolean {
        return if (ballSquash.ballPositionX < 0 - ballSquash.ballSize) {
            soundEffect.play(soundEffectsList[2])
            gameOver = true
            true
        } else {
            false
        }
    }

    fun start() {
        running = true
        thread =
            Thread(this)
        thread?.start()

    }

    fun stop() {
        running = false
        try {
            thread?.interrupt()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun update() {
        ballIntersects(ballSquash, squashPad)
        val screenHeight = height
        ballSquash.update()
        // Räknar när boll rör långsidan
        if (ballSquash.ballPositionX > width - ballSquash.ballSize) {
            updateScore()
            soundEffect.play(soundEffectsList[0]) //ljudeffekt när boll rör långsidan
        }
        if (ballSquash.ballPositionY > height - ballSquash.ballSize) {
            soundEffect.play(soundEffectsList[0]) //ljudeffekt när boll rör golvet
        }
        if (ballSquash.ballPositionY < 0 + ballSquash.ballSize) {
            soundEffect.play(soundEffectsList[0]) //ljudeffekt när boll rör taket
        }
    }

    override fun run() {
        while (running) {
            if (!isPaused) {
                try {
                    update()
                    drawGameSquash(holder)
                    ballSquash.checkBounds(bounds)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            val x = event.x.toInt()
            val y = event.y.toInt()

            if (isInsidePauseRectangule(x, y) && event.action == MotionEvent.ACTION_DOWN) {
//Klickar på paus
                buttonPauseText = if (isPaused) context.getString(R.string.pauseText)
                else context.getString(R.string.resumeText)
                isPaused = !isPaused
            } else if (isInsideRestartRectangle(x, y) && event.action == MotionEvent.ACTION_DOWN) {
//Klickar på restart
                isPaused = false
                restartGame()
            } else {

                squashPad.ballPositionY = event.y
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun isInsidePauseRectangule(x: Int, y: Int) = buttonPauseRect!!.contains(x, y)

    private fun isInsideRestartRectangle(x: Int, y: Int): Boolean {
        return buttonRestartRect?.contains(x, y) ?: false
    }

    private fun restartGame() {
        stop()
        score = 0
        isGameWon = false
        isPaused = false
        ballSquash.reset()
        squashPad.ballPositionX = 50f
        squashPad.ballPositionY = 400f

        buttonPauseText = context.getString(R.string.pauseText)

        drawGameSquash(holder)

        start()
    }


    fun onBallCollision(ballSquash1: BallSquash, squashPad: SquashPad) {

        val relativeIntersectY = squashPad.ballPositionY - ballSquash1.ballPositionY
        val normalizedIntersectY = (relativeIntersectY / (squashPad.height / 2)).coerceIn(-1f, 1f)
        val bounceAngle =
            normalizedIntersectY * Math.PI / 7


        val randomSpeed = (15..45).random().toFloat()
        ballSquash1.ballSpeedX = (randomSpeed * Math.cos(bounceAngle)).toFloat()
        ballSquash1.ballSpeedY = (-randomSpeed * Math.sin(bounceAngle)).toFloat()

    }

    fun ballIntersects(ballSquash1: BallSquash, squashPad: SquashPad) {
        val padLeft = squashPad.ballPositionX - squashPad.width
        val padRight = squashPad.ballPositionX + squashPad.width
        val padTop = squashPad.ballPositionY - squashPad.height
        val padBottom = squashPad.ballPositionY + squashPad.height
        val ballLeft = ballSquash1.ballPositionX - ballSquash1.ballSize
        val ballRight = ballSquash1.ballPositionX + ballSquash1.ballSize
        val ballTop = ballSquash1.ballPositionY - ballSquash1.ballSize
        val ballBottom = ballSquash1.ballPositionY + ballSquash1.ballSize

        if (ballRight >= padLeft && ballLeft <= padRight && ballBottom >= padTop && ballTop <=
            padBottom
        ) {
            onBallCollision(ballSquash1, squashPad)
            soundEffect.play(soundEffectsList[1])
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        gameBoundaryPath = createBoundaryPath(width, height)
        bounds = Rect(0, 0, width, height)
        start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stop()
    }


    fun drawGameSquash(holder: SurfaceHolder) {
        val canvas: Canvas? = holder.lockCanvas()
        canvas?.drawColor(Color.BLACK)

        gameBoundaryPath?.let {
            canvas?.drawPath(it, lineColor)

            if (isGameOver()) {
//Användare förlorar
                canvas?.drawPath(it, touchColor)
                canvas?.drawText(
                    "Score: $score",
                    canvas.width.toFloat() - 400,
                    0f + 100,
                    textGameOverPaint
                )
                canvas?.drawText(
                    "GAME OVER",
                    canvas.width.toFloat() / 3,
                    canvas.height.toFloat() / 2,
                    textGameOverPaint
                )
//Sparar poäng vid förlust
                val sharedPreferencesManager: DataManager = SharedPreferencesManager(context)
                sharedPreferencesManager.addNewScore(
                    DataManager.Score(
                        this.userName,
                        score,
                        DataManager.Game.SQUASH
                    )
                )
            } else if (isGameWon) {
//Sparar poäng vid vinst
                canvas?.drawText(
                    "Congratz! You Won",
                    canvas.width.toFloat() / 6,
                    canvas.height.toFloat() / 2,
                    textGameOverPaint
                )
                val sharedPreferencesManager: DataManager = SharedPreferencesManager(context)
                sharedPreferencesManager.addNewScore(
                    DataManager.Score(
                        this.userName,
                        score,
                        DataManager.Game.SQUASH
                    )
                )

                holder.unlockCanvasAndPost(canvas)

                stop()
                return
            } else {
//Ritar upp poäng
                canvas?.drawText(
                    "Score: $score",
                    canvas.width.toFloat() - 400,
                    0f + 100,
                    scorePaint
                )
            }
        }

        ballSquash.draw(canvas)
        squashPad.draw(canvas)

//Ritar upp Pausknapp
        if (canvas != null) {
            val buttonPauseTextLength = buttonPauseTextPaint.measureText(buttonPauseText)
            buttonPauseRect = Rect(
                (canvas.width.toFloat() - 650f).toInt(),
                0 + 50,
                (canvas.width.toFloat() - 650f + buttonPauseTextLength + 10).toInt(),
                50 + textGameOverPaint.textSize.toInt()
            )

            canvas.drawRect(buttonPauseRect!!, buttonPausePaint)
            canvas.drawText(
                buttonPauseText,
                buttonPauseRect!!.left.toFloat() + 5,
                buttonPauseRect!!.top.toFloat() + buttonPauseTextPaint.textSize - 10,
                buttonPauseTextPaint
            )
        }
//Ritar upp restartknapp
        if (canvas != null) {
            val buttonRestartTextLength = buttonRestartTextPaint.measureText(buttonRestartText)
            buttonRestartRect = Rect(
                buttonPauseRect!!.left - buttonRestartTextLength.toInt() - 30, // 30 is the space between the buttons
                buttonPauseRect!!.top,
                buttonPauseRect!!.left - 10, // 10 is for padding
                buttonPauseRect!!.bottom
            )

            canvas.drawRect(buttonRestartRect!!, buttonRestartPaint)
            canvas.drawText(
                buttonRestartText,
                buttonRestartRect!!.left.toFloat() + 5,
                buttonRestartRect!!.top.toFloat() + buttonRestartTextPaint.textSize - 10,
                buttonRestartTextPaint
            )
        }

        holder.unlockCanvasAndPost(canvas)

        if (checkGameOver()) {
            stop()
        }
    }


    private fun isGameOver() = ballSquash.ballPositionX < 0 - ballSquash.ballSize

//För syns skull gör en Path med färgade linjer för gränserna.
    private fun createBoundaryPath(width: Int, height: Int): Path {
        val path = Path()
        path.moveTo(0f, 0f)
        path.lineTo(width.toFloat(), 0f)
        path.lineTo(width.toFloat(), height.toFloat())
        path.lineTo(0f, height.toFloat())
        return path
    }

    private var isGameWon = false

    private fun updateScore(): Int {
        score++
        if (score >= 15) {
            isGameWon = true
            soundEffect.play(soundEffectsList[3])
            return score
        }
        if (isGameOver() == true) {
            soundEffect.play(soundEffectsList[2])
        }
        return score
    }
}