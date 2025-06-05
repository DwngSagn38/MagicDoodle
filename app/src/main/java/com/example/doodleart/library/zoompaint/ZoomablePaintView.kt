package com.example.doodleart.library.zoompaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import java.util.LinkedList
import java.util.Queue
import java.util.Stack

class ZoomablePaintView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs), ScaleGestureDetector.OnScaleGestureListener {

    private val drawPaint = Paint().apply {
        color = Color.RED
        strokeWidth = 10f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private var originalBitmap: Bitmap? = null
    private var overlayBitmap: Bitmap? = null
    private var overlayCanvas: Canvas? = null

    private var posX = 0f
    private var posY = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isDragging = false
    private var hue = 0f

    private var isPreviewMode = false
    private val path = android.graphics.Path()
    private var scaleFactor = 1f
    private var scaleDetector = ScaleGestureDetector(context, this)

    private var drawingBitmap: Bitmap? = null
    private var drawingCanvas: Canvas? = null
    private var isEraserMode = false
    private var isFloodFillMode = false
    private val undoStack = Stack<Bitmap>()
    init {
        // Optional: Init bitmap with background image
    }

    fun toggleFloodFillMode() {
        isFloodFillMode = !isFloodFillMode
    }

    fun setBrushColor(color: Int) {
        drawPaint.color = color
    }

    fun setBrushSize(sizePx: Int) {
        drawPaint.strokeWidth = sizePx.toFloat()
    }

    fun setPreviewMode(enabled: Boolean) {
        isPreviewMode = enabled
        invalidate()
    }



    private val mainBitmap: Bitmap?
        get() = originalBitmap


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        drawingBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawingCanvas = Canvas(drawingBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(posX, posY)
        canvas.scale(scaleFactor, scaleFactor)

        originalBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        overlayBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        canvas.drawPath(path, drawPaint)

        canvas.restore()
    }



    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)

        val x = event.x
        val y = event.y
        val pointerCount = event.pointerCount

        if (isPreviewMode) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    lastTouchX = x
                    lastTouchY = y
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = x - lastTouchX
                    val dy = y - lastTouchY
                    posX += dx
                    posY += dy
                    lastTouchX = x
                    lastTouchY = y
                    invalidate()
                }
            }
            return true
        }

        // Chế độ vẽ hoặc flood fill
        val touchX = (x - posX) / scaleFactor
        val touchY = (y - posY) / scaleFactor

        if (pointerCount == 1 && !scaleDetector.isInProgress) {
            if (isFloodFillMode && event.action == MotionEvent.ACTION_DOWN) {
                saveToUndoStack() // ✅ Chỉ lưu khi thực sự thực hiện flood fill
                floodFill(overlayBitmap!!, originalBitmap!!, touchX.toInt(), touchY.toInt(), drawPaint.color)

                return true
            }

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isDragging = true
                    lastTouchX = x
                    lastTouchY = y
                    path.moveTo(touchX, touchY)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isDragging) {
                        path.lineTo(touchX, touchY)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (isDragging) {
                        saveToUndoStack()
                        overlayCanvas?.drawPath(path, drawPaint)
                        path.reset()
                        isDragging = false
                    }
                }
            }
        }

        invalidate()
        return true
    }



    // Zoom
    override fun onScale(detector: ScaleGestureDetector): Boolean {
        scaleFactor *= detector.scaleFactor
        scaleFactor = scaleFactor.coerceIn(0.5f, 5f)
        invalidate()
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector) = true
    override fun onScaleEnd(detector: ScaleGestureDetector) {}

    private fun floodFill(
        overlay: Bitmap,
        reference: Bitmap,
        x: Int,
        y: Int,
        replacementColor: Int,
        tolerance: Int = 30
    ) {
        val width = overlay.width
        val height = overlay.height

        // ⚠ Kiểm tra trước khi truy cập pixel
        if (x !in 0 until width || y !in 0 until height) return

        val targetColor = reference.getPixel(x, y)
        if (areColorsSimilar(targetColor, replacementColor, tolerance)) return

        val queue = ArrayDeque<Pair<Int, Int>>()
        val visited = HashSet<Pair<Int, Int>>()
        queue.add(Pair(x, y))

        while (queue.isNotEmpty()) {
            val (px, py) = queue.removeFirst()
            if (px !in 0 until width || py !in 0 until height) continue
            if (!visited.add(Pair(px, py))) continue

            val currentColor = reference.getPixel(px, py)
            if (!areColorsSimilar(currentColor, targetColor, tolerance)) continue

            overlay.setPixel(px, py, replacementColor)
            queue.add(Pair(px + 1, py))
            queue.add(Pair(px - 1, py))
            queue.add(Pair(px, py + 1))
            queue.add(Pair(px, py - 1))
        }

        invalidate()
    }


    fun areColorsSimilar(color1: Int, color2: Int, tolerance: Int): Boolean {
        val r1 = Color.red(color1)
        val g1 = Color.green(color1)
        val b1 = Color.blue(color1)
        val r2 = Color.red(color2)
        val g2 = Color.green(color2)
        val b2 = Color.blue(color2)
        val dr = r1 - r2
        val dg = g1 - g2
        val db = b1 - b2
        val distance = dr * dr + dg * dg + db * db
        return distance <= tolerance * tolerance
    }

    fun loadImage(bitmap: Bitmap) {
        post {
            val viewWidth = width
            val viewHeight = height

            val bitmapWidth = bitmap.width
            val bitmapHeight = bitmap.height

            val widthRatio = viewWidth.toFloat() / bitmapWidth
            val heightRatio = viewHeight.toFloat() / bitmapHeight
            val scale = minOf(widthRatio, heightRatio)

            val scaledWidth = (bitmapWidth * scale).toInt()
            val scaledHeight = (bitmapHeight * scale).toInt()

            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)

            originalBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888)
            val originalCanvas = Canvas(originalBitmap!!)
            val left = (viewWidth - scaledWidth) / 2f
            val top = (viewHeight - scaledHeight) / 2f
            originalCanvas.drawBitmap(resizedBitmap, left, top, null)

            overlayBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888)
            overlayCanvas = Canvas(overlayBitmap!!)

            undoStack.clear()
            saveToUndoStack()
            invalidate()
        }
    }




    private fun saveToUndoStack() {
        overlayBitmap?.let {
            undoStack.push(it.copy(Bitmap.Config.ARGB_8888, true))
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            overlayBitmap = undoStack.pop()
            overlayCanvas = Canvas(overlayBitmap!!)
            invalidate()
        }
    }


    fun setEraserMode(enabled: Boolean) {
        isEraserMode = enabled
        if (enabled) {
            drawPaint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR)
            drawPaint.alpha = 0
        } else {
            drawPaint.xfermode = null
            drawPaint.alpha = 255
        }
    }

    fun resetZoomAndPan() {
        scaleFactor = 1f
        posX = 0f
        posY = 0f
        invalidate()
    }

    private fun drawBlingEffect(x: Float, y: Float) {
        val sparklePaint = Paint().apply {
            shader = RadialGradient(
                x, y, 20f,
                intArrayOf(Color.WHITE, Color.TRANSPARENT),
                floatArrayOf(0.1f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        overlayCanvas?.drawCircle(x, y, 20f, sparklePaint)
    }

    private fun drawStarryEffect(x: Float, y: Float) {
        val starPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        for (i in 0..5) {
            val offsetX = (-10..10).random()
            val offsetY = (-10..10).random()
            val size = (1..3).random().toFloat()
            overlayCanvas?.drawCircle(x + offsetX, y + offsetY, size, starPaint)
        }
    }
    private fun updateRainbowColor() {
        hue += 5f
        if (hue >= 360f) hue = 0f
        val hsv = floatArrayOf(hue, 1f, 1f)
        drawPaint.color = Color.HSVToColor(hsv)
    }


}


