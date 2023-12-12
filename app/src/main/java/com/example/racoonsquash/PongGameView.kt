package com.example.racoonsquash

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.view.SurfaceHolder
import android.view.SurfaceView

class PongGameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback, Runnable {
    private var thread: Thread? = null
    private var running = false
    private var lineColor: Paint
    private var leftBoundaryPath: Path? = null
    private var rightBoundaryPath: Path? = null
    lateinit var pongball: Ball
    lateinit var canvas: Canvas
    private var isPaused = false
    private var bounds = Rect()
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
        pongball = Ball(this.context, 100f, 100f, 30f, 5f, 5f, Color.BLUE, 20f)
        // ...
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



    override fun surfaceCreated(holder: SurfaceHolder) {

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        leftBoundaryPath = createBoundaryPathLeft(width, height)
        rightBoundaryPath = createBoundaryPathRight(width, height)
        drawGameBounds(holder)
//        start()

    }


    override fun surfaceDestroyed(holder: SurfaceHolder) {
//        stop()
    }
    fun update() {
            pongball.update()

    }
    override fun run() {
        while(running) {
                pongball.update()
                drawGameBounds(holder)
                pongball.checkBounds(bounds) // Kontrollera gränserna för bollen

        }

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
        pongball.draw(canvas)
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