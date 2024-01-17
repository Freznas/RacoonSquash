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
    val soundEffect = SoundEffect(context) // Behöver komma åt i activity för att frigöra resurser.

    //Path-klass ritar ett "spår" från en punkt moveTo() till nästa punkt lineTo()
    private var gameBoundaryPath: Path? = null

    var bounds = Rect() //for att kunna studsa m vaggarna
    var mHolder: SurfaceHolder? = holder

    private var gameOver = false

    private var buttonPauseRect: Rect? = null
    private val buttonPausePaint = Paint().apply {
        //This could be transparent if no rectangle is wanted to be shown
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

        ballSquash = BallSquash(this.context, 100f, 100f, 30f, 20f, 20f, Color.RED, 20f)

        // val drawablePaddle = resources.getDrawable(R.drawable.player_pad_a, null)
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
            Thread(this) //en trad har en konstruktor som tar in en runnable,
        // vilket sker i denna klass se rad 10
        thread?.start()

    }

    fun stop() {
        running = false
        try {
            thread?.interrupt() //join betyder att huvudtraden komemr vanta in att traden dor ut av sig sjalv
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun update() { //bounce sounds utanfor spelplanen nar fatt gameOver. Pauseknappen borde vara nere
        ballIntersects(ballSquash, squashPad)
        val screenHeight = height // Höjden på skärmen
        ballSquash.update()
        // Räknar bara när boll rör långsidan just nu
        if (ballSquash.ballPositionX > width - ballSquash.ballSize) {
            updateScore()
            soundEffect.play(soundEffectsList[0]) //ljudeffekt när boll rör långsidan
        }
        if (ballSquash.ballPositionY > height - ballSquash.ballSize) {
            soundEffect.play(soundEffectsList[0]) //ljudeffekt när boll rör golvet
        }
        if (ballSquash.ballPositionY < 0 + ballSquash.ballSize) {
            soundEffect.play(soundEffectsList[0]) //ljudeffekt när boll rör golvet
        }
    }

    override fun run() {
        while (running) {
            if (!isPaused) {
                try {
                    update()
                    drawGameBounds(holder)
                    ballSquash.checkBounds(bounds)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //med denna kod kan jag rora pa boll2 som star stilla annars
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            val x = event.x.toInt()
            val y = event.y.toInt()

            if (isInsidePauseRectangule(x, y) && event.action == MotionEvent.ACTION_DOWN) {
                // User is clicking on Pause
                buttonPauseText = if (isPaused) context.getString(R.string.pauseText)
                else context.getString(R.string.resumeText)
                isPaused = !isPaused
               // drawGameBounds(holder) // causing bug when thread is stopped
            } else if (isInsideRestartRectangle(x, y) && event.action == MotionEvent.ACTION_DOWN) {
                // User is clicking on Restart
                isPaused = false
                restartGame()
            } else {
                // User is not clicking on pause or restart - handle other touch events
                squashPad.ballPositionY = event.y // Move pad
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun isInsidePauseRectangule(x: Int, y: Int) = buttonPauseRect!!.contains(x, y)

    private fun isInsideRestartRectangle(x: Int, y: Int): Boolean {
        // Implement this method to return true if the (x, y) coordinates are inside the restart button rectangle
        return buttonRestartRect?.contains(x, y) ?: false
    }

    private fun restartGame() {
        stop() // interrupt current thread
        score = 0
        isGameWon = false
        isPaused = false
        // Reset ball position, speed, and paddle size
        ballSquash.reset() // Implement this method in your BallSquash class
        //squashPad.reset() // Implement this method in your SquashPad class

        squashPad.ballPositionX = 50f // Initial X position as defined in setup()
        squashPad.ballPositionY = 400f // Initial Y position as defined in setup()

        buttonPauseText = context.getString(R.string.pauseText)

        drawGameBounds(holder)

        start() // start new thread
    }

    //onBallCollision inverterar riktningen på bollen när den träffar squashPad
// denna funktionen beräknar avstånd från bollens Y position och padelns Y position för att
// bestämma vart på padeln som bollen träffar.
// sen bestäms studsriktningen beroende på vart på padeln kollisionen sker
// sen så räknas vinkeln genom multiplicera normaliserade värdet.
    fun onBallCollision(ballSquash1: BallSquash, squashPad: SquashPad) {

        val relativeIntersectY = squashPad.ballPositionY - ballSquash1.ballPositionY
        val normalizedIntersectY = (relativeIntersectY / (squashPad.height / 2)).coerceIn(-1f, 1f)
        val bounceAngle =
            normalizedIntersectY * Math.PI / 7


        val randomSpeed = (15..45).random().toFloat()
        ballSquash1.ballSpeedX = (randomSpeed * Math.cos(bounceAngle)).toFloat()
        ballSquash1.ballSpeedY = (-randomSpeed * Math.sin(bounceAngle)).toFloat()
        // För att få slumpmässig hastighet vid kollision mellan boll o paddel ta koden ↑
        //För att ha en hastighet så ta koden ↓
//        ballSquash1.ballSpeedX = (ballSquash1.speed * Math.cos(bounceAngle)).toFloat()
//        ballSquash1.ballSpeedY = (-ballSquash1.speed * Math.sin(bounceAngle)).toFloat()
    }

    // här tar vi in storlek från ball och squashPad och kontrollerar när en kollision sker
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
            soundEffect.play(soundEffectsList[1]) //ljudeffekt när boll rör pad
        }
    }


    //dessa startar och stoppar min thread:
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

    //run/metoden ar en metod som vi fick fran interface Runnable och ar kopplat till dess Thread.
    // Run anropas nar vi kor Thread.start()
    //den kor en while loop med vår running variable och anropar update och draw:


    fun drawGameBounds(holder: SurfaceHolder) {
        val canvas: Canvas? = holder.lockCanvas()
        canvas?.drawColor(Color.BLACK)

        gameBoundaryPath?.let {
            canvas?.drawPath(it, lineColor)

            if (isGameOver()) {
                // User loses
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

                // Save score
                val sharedPreferencesManager: DataManager = SharedPreferencesManager(context)
                sharedPreferencesManager.addNewScore(
                    DataManager.Score(
                        this.userName,
                        score,
                        DataManager.Game.SQUASH
                    )
                )
            } else if (isGameWon) {
                // Player wins
                canvas?.drawText(
                    "Congratz! You Won",
                    canvas.width.toFloat() / 6,
                    canvas.height.toFloat() / 2,
                    textGameOverPaint
                )
                // Save score
                val sharedPreferencesManager: DataManager = SharedPreferencesManager(context)
                sharedPreferencesManager.addNewScore(
                    DataManager.Score(
                        this.userName,
                        score,
                        DataManager.Game.SQUASH
                    )
                )

                // Unlock and post canvas before stopping the game
                holder.unlockCanvasAndPost(canvas)

                stop() // Stop the game
                return // Return early to prevent further drawing
            } else {
                // Normal gameplay score display
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

        // Draw pause button
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

    // För syns skull gör en Path med färgade linjer för gränserna.
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
        if(isGameOver() == true) {
            soundEffect.play(soundEffectsList[2])
        }
        return score
    }
}

//        if ()
//        {
//            ballSquash1.ballSpeedY *= -1
//            ballSquash1.ballSpeedX= (10..30).random().toFloat()
//        }