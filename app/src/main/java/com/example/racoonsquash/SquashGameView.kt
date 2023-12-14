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


class SquashGameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback, Runnable {
    private var thread: Thread? = null
    private var running = false
    lateinit var ballSquash: BallSquash
    lateinit var squashPad: SquashPad
    private var lineColor: Paint
    private var touchColor: Paint
    private var scorePaint: Paint
    private var textGameOverPaint: Paint
    private var score: Int = 0;
    private var isPaused = false



    //Path-klass ritar ett "spår" från en punkt moveTo() till nästa punkt lineTo()
    private var gameBoundaryPath: Path? = null

    var bounds = Rect() //for att kunna studsa m vaggarna
    var mHolder: SurfaceHolder? = holder

    private var buttonPauseRect :Rect? = null
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

    init {
        if (mHolder != null) {
            mHolder?.addCallback(this)
        }

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
            thread?.join() //join betyder att huvudtraden komemr vanta in att traden dor ut av sig sjalv
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun update() {
        ballIntersects(ballSquash, squashPad)
        ballSquash.update()
        // Räknar bara när boll rör långsidan just nu
        if (ballSquash.posX > width - ballSquash.size) {
            updateScore()
        } else if (ballSquash.posX < 0) {
            score = 0
        }
    }

    override fun run() {
        while (running) {
            if(!isPaused) {
                update()
                drawGameBounds(holder)
                ballSquash.checkBounds(bounds)
            }
        }
    }

    //med denna kod kan jag rora pa boll2 som star stilla annars
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
         if (event != null) {
            val x = event.x.toInt()
            val y = event.y.toInt()
            if (!isInsidePauseRectangule(x, y)) {
                // User is not clicking on pause
                squashPad.posY = event.y // Move pad
            } else if (isInsidePauseRectangule(x, y) && event.action == MotionEvent.ACTION_DOWN) {
                // User is clicking on Pause
                buttonPauseText = if (isPaused) context.getString(R.string.pauseText)
                                    else context.getString(R.string.resumeText)
                isPaused = !isPaused
                drawGameBounds(holder)
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun isInsidePauseRectangule(x: Int, y: Int) = buttonPauseRect!!.contains(x, y)

//onBallCollision inverterar riktningen på bollen när den träffar squashPad
// denna funktionen beräknar avstånd från bollens Y position och padelns Y position för att
// bestämma vart på padeln som bollen träffar.
// sen bestäms studsriktningen beroende på vart på padeln kollisionen sker
// sen så räknas vinkeln genom multiplicera normaliserade värdet.
    fun onBallCollision(ballSquash1: BallSquash, squashPad: SquashPad) {

        val relativeIntersectY = squashPad.posY - ballSquash1.posY
        val normalizedIntersectY = (relativeIntersectY / (squashPad.height / 2)).coerceIn(-1f, 1f)
        val bounceAngle =
            normalizedIntersectY * Math.PI / 7

        ballSquash1.speedX = (ballSquash1.speed * Math.cos(bounceAngle)).toFloat()
        ballSquash1.speedY = (-ballSquash1.speed * Math.sin(bounceAngle)).toFloat()
    }

    // här tar vi in storlek från ball och squashPad och kontrollerar när en kollision sker
    fun ballIntersects(ballSquash1: BallSquash, squashPad: SquashPad) {
        val padLeft = squashPad.posX - squashPad.width
        val padRight = squashPad.posX + squashPad.width
        val padTop = squashPad.posY - squashPad.height
        val padBottom = squashPad.posY + squashPad.height
        val ballLeft = ballSquash1.posX - ballSquash1.size
        val ballRight = ballSquash1.posX + ballSquash1.size
        val ballTop = ballSquash1.posY - ballSquash1.size
        val ballBottom = ballSquash1.posY + ballSquash1.size

        if (ballRight >= padLeft && ballLeft <= padRight && ballBottom >= padTop && ballTop <=
            padBottom
        ) {
            onBallCollision(ballSquash1, squashPad)
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

            if (ballSquash.posX < 0 - ballSquash.size) {
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

            } else {
                // Placera text
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

        //Draw pause button
        if (canvas != null) {
            val buttonPauseTextLength = buttonPauseTextPaint.measureText(buttonPauseText)
            buttonPauseRect = Rect(
                (canvas.width.toFloat() - 650f).toInt(),
                0 + 50,
                (canvas.width.toFloat() - 650f + buttonPauseTextLength + 10).toInt(),
                50 + textGameOverPaint.textSize.toInt()
            ) // x-start, y-start, x-end, y-end

            canvas.drawRect(buttonPauseRect!!, buttonPausePaint)
            canvas.drawText(
                buttonPauseText,
                buttonPauseRect!!.left.toFloat() + 5,
                buttonPauseRect!!.top.toFloat() + buttonPauseTextPaint.textSize - 10,
                buttonPauseTextPaint
            )
        }
        holder.unlockCanvasAndPost(canvas)
    }

    // För syns skull gör en Path med färgade linjer för gränserna.
    private fun createBoundaryPath(width: Int, height: Int): Path {
        val path = Path()
        path.moveTo(0f, 0f)
        path.lineTo(width.toFloat(), 0f)
        path.lineTo(width.toFloat(), height.toFloat())
        path.lineTo(0f, height.toFloat())
        return path
    }

    private fun updateScore(): Int {
        score++
        return score
    }
}