package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Typeface
import android.icu.text.Transliterator
import android.view.MotionEvent

class SquashGameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback, Runnable {
    private var thread: Thread? = null
    private var running = false
    lateinit var canvas: Canvas
    lateinit var ball1: Ball
    lateinit var squashPad: SquashPad
    lateinit var posX: Transliterator.Position
    lateinit var posY: Transliterator.Position
    private var lineColor: Paint
    private var touchColor: Paint
    private var scorePaint: Paint
    private var textGameOverPaint: Paint
    private var score: Int = 0;

    //Path-klass ritar ett "spår" från en punkt moveTo() till nästa punkt lineTo()
    private var gameBoundaryPath: Path? = null

    var bounds = Rect() //for att kunna studsa m vaggarna
    var mHolder: SurfaceHolder? = holder

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

    private fun setup() {
        ball1 = Ball(this.context, 100f, 100f, 30f, 20f, 7f, Color.RED)
        val drawablePaddle = resources.getDrawable(R.drawable.player_pad, null)
        squashPad = SquashPad(
            this.context, 50f, 400f, 6f, 0f, 0f, 0,
            4f, 75f
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
        ballIntersects(ball1, squashPad)
        ball1.update()
        // Räknar bara när boll rör långsidan just nu
        if (ball1.posX > width - ball1.size) {
            updateScore()
        } else if (ball1.posX < 0) {
            score = 0
        }
    }

    //med denna kod kan jag rora pa boll2 som star stilla annars
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val vX =
            event?.x.toString() //ett sätt att hantera att en vill ha null och en nonnull
        // versioner av datatyperna
        val vY = event?.y.toString()
        squashPad.posY = event!!.y
        return true
        // return super.onTouchEvent(event)
    }

    //onBallCollision inverterar riktningen på bollen när den träffar squashPad
    fun onBallCollision(ball1: Ball, squashPad: SquashPad) {
        ball1.speedY *= -1
        ball1.speedX *= -1
    }

    // här tar vi in storlek från ball och squashPad och kontrollerar när en kollision
// sker och kallar onBallCollision
    fun ballIntersects(ball1: Ball, squashPad: SquashPad) {
        val padLeft = squashPad.posX - squashPad.width
        val padRight = squashPad.posX + squashPad.width
        val padTop = squashPad.posY - squashPad.height
        val padBottom = squashPad.posY + squashPad.height

        val ballLeft = ball1.posX - ball1.size
        val ballRight = ball1.posX + ball1.size
        val ballTop = ball1.posY - ball1.size
        val ballBottom = ball1.posY + ball1.size

        if (ballRight >= padLeft && ballLeft <= padRight && ballBottom >= padTop && ballTop <=
            padBottom
        ) {
            onBallCollision(ball1, squashPad)
        }
    }
    /*    fun draw() {
            canvas = holder!!.lockCanvas()
            canvas.drawColor(Color.BLUE)


            holder!!.unlockCanvasAndPost(canvas)
        }*/

    //dessa startar och stoppar min thread:
    override fun surfaceCreated(holder: SurfaceHolder) {
        // start()
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
    //den kor en while loop med var running variable pch anropar update och draw:
    override fun run() {
        while (running) {
            update()
            drawGameBounds(holder)
            ball1.checkBounds(bounds)
        }
    }

    fun drawGameBounds(holder: SurfaceHolder) {
        val canvas: Canvas? = holder.lockCanvas()
        canvas?.drawColor(Color.BLACK)

        gameBoundaryPath?.let {
            canvas?.drawPath(it, lineColor)

            if (ball1.posX < 0 - ball1.size) {
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
                canvas?.drawText("Score: $score", canvas.width.toFloat() - 400, 0f + 100, scorePaint)
            }
        }

        ball1.draw(canvas)
        squashPad.draw(canvas)
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