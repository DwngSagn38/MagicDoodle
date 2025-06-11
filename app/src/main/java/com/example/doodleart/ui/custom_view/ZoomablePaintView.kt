package com.example.doodleart.ui.custom_view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import java.util.Stack
import kotlin.math.sqrt

class ZoomablePaintView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs), ScaleGestureDetector.OnScaleGestureListener {

    private val drawPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        strokeWidth = 10f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private var drawingBitmap: Bitmap? = null
    private var drawingCanvas: Canvas? = null

    private var posX = 0f
    private var posY = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isDragging = false
    private var scaleFactor = 1f
    private val scaleDetector = ScaleGestureDetector(context, this)
    private val path = Path()
    private val livePath = Path()

    private var isPreviewMode = false
    private var isFloodFillMode = false
    private var isBlingMode = false
    private val undoStack = Stack<Bitmap>()
    private val redoStack = Stack<Bitmap>()
    var undoCount = 0
        private set
    var redoCount = 0
        private set

    private var sourceBitmap: Bitmap? = null

    private val COLOR_BLING = Color.MAGENTA


    fun setFloodFillMode(enabled: Boolean) {
        isFloodFillMode = enabled
    }

    fun setBlingMode(enabled: Boolean) {
        isBlingMode = enabled
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (drawingBitmap == null) {
            drawingBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            drawingCanvas = Canvas(drawingBitmap!!)
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()

        // Áp dụng pan + zoom trước
        canvas.translate(posX, posY)
        canvas.scale(scaleFactor, scaleFactor)

        // Tạo vùng clip bo góc
        val cornerRadius = 90f // tuỳ chỉnh độ bo góc
        val clipPath = Path().apply {
            addRoundRect(
                0f, 0f,
                width.toFloat(),
                height.toFloat(),
                cornerRadius,
                cornerRadius,
                Path.Direction.CW
            )
        }
        canvas.clipPath(clipPath)

        // Vẽ ảnh và path
        drawingBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
        canvas.drawPath(path, drawPaint)

        if (isDragging) {
            canvas.drawPath(livePath, drawPaint)
        }
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)

        val x = event.x
        val y = event.y
        val pointerCount = event.pointerCount

//        when (event.actionMasked) {
//            MotionEvent.ACTION_POINTER_DOWN -> {
//                if (event.pointerCount == 2) {
//                    isPreviewMode = true
//                    invalidate()
//                    return true
//                }
//            }
//            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                if (event.pointerCount < 2) {
//                    isPreviewMode = false
//                    invalidate()
//                }
//            }
//        }

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

                    if (isOutOfBounds()) {
                        resetZoomAndPan()
                    }
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
                val tx = touchX.toInt()
                val ty = touchY.toInt()
                if (tx in 0 until (drawingBitmap?.width ?: 0) &&
                    ty in 0 until (drawingBitmap?.height ?: 0)) {
                    saveToUndoStack()
                    floodFill(drawingBitmap!!, tx, ty, drawPaint.color)
                }
                return true
            }


            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isDragging = true
                    lastTouchX = x
                    lastTouchY = y
                    path.moveTo(touchX, touchY)
                    livePath.moveTo(x, y)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isDragging) path.lineTo(touchX, touchY)
                    livePath.lineTo(x, y)
                }
                MotionEvent.ACTION_UP -> {
                    if (isDragging) {
                        saveToUndoStack()

                        val tempBitmap = Bitmap.createBitmap(drawingBitmap!!.width, drawingBitmap!!.height, Bitmap.Config.ARGB_8888)
                        val tempCanvas = Canvas(tempBitmap)
                        tempCanvas.drawPath(path, drawPaint)

                        val paintColor = drawPaint.color

                        for (y in 0 until tempBitmap.height) {
                            for (x in 0 until tempBitmap.width) {
                                val drawnPixel = tempBitmap.getPixel(x, y)
                                if (drawnPixel != Color.TRANSPARENT) {
                                    val bgPixel = sourceBitmap?.getPixel(x, y) ?: Color.WHITE

                                    if (Color.alpha(bgPixel) < 50) continue

                                    if (isNearBlack(bgPixel, 40)) {
                                        if (paintColor == Color.BLACK) {
                                            drawingBitmap?.setPixel(x, y, Color.WHITE) // biến nét đen sẵn thành trắng
                                        }
                                        if (paintColor != Color.BLACK) {
                                            drawingBitmap?.setPixel(x, y, Color.BLACK)
                                        }

                                        continue
                                    }

                                    drawingBitmap?.setPixel(x, y, paintColor)
                                }
                            }
                        }

                        if (isBlingMode) {
                            val blingPaint = Paint().apply {
                                color = Color.WHITE
                                style = Paint.Style.FILL
                                isAntiAlias = true
                            }

                            val blingPathBitmap = Bitmap.createBitmap(drawingBitmap!!.width, drawingBitmap!!.height, Bitmap.Config.ARGB_8888)
                            val blingCanvas = Canvas(blingPathBitmap)
                            blingCanvas.drawPath(path, drawPaint)

                            // Lặp qua bitmap chứa stroke để tìm điểm cần vẽ bling
                            for (by in 0 until blingPathBitmap.height) {
                                for (bx in 0 until blingPathBitmap.width) {
                                    val pixel = blingPathBitmap.getPixel(bx, by)
                                    if (pixel != Color.TRANSPARENT && Math.random() < 0.001) {
                                        val radius = 1f + Math.random().toFloat() * 2f
                                        drawingCanvas?.drawCircle(bx.toFloat(), by.toFloat(), radius, blingPaint)
                                    }
                                }
                            }
                        }
                        livePath.reset()
                        path.reset()
                        isDragging = false
                    }
                }

            }
        }

        invalidate()
        return true
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val prevScale = scaleFactor
        scaleFactor *= detector.scaleFactor
        scaleFactor = scaleFactor.coerceIn(0.5f, 5f)

        // Zoom theo tâm tay chạm
        val focusX = detector.focusX
        val focusY = detector.focusY

        val scaleChange = scaleFactor / prevScale
        posX = (posX - focusX) * scaleChange + focusX
        posY = (posY - focusY) * scaleChange + focusY

        invalidate()
        return true
    }


    override fun onScaleBegin(detector: ScaleGestureDetector) = true
    override fun onScaleEnd(detector: ScaleGestureDetector) {}

    fun loadImage(bitmap: Bitmap) {
        post {
            val viewWidth = width
            val viewHeight = height

            if (viewWidth == 0 || viewHeight == 0) return@post

            // Scale ảnh phù hợp view, lưu lại
            val scaled = Bitmap.createScaledBitmap(bitmap, width, height, false)
            sourceBitmap = scaled

            // 1. Tạo bitmap mới trắng toàn bộ
            val whiteBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val color = scaled.getPixel(x, y)
                    whiteBitmap.setPixel(
                        x, y,
                        if (isNearBlack(color)) Color.BLACK else Color.WHITE
                    )
                }
            }

            // 2. Cập nhật bitmap vẽ và canvas
            drawingBitmap = whiteBitmap.copy(Bitmap.Config.ARGB_8888, true)
            drawingCanvas = Canvas(drawingBitmap!!)

            undoStack.clear()
            saveToUndoStack()
            invalidate()
        }
    }


    private fun floodFill(bitmap: Bitmap, x: Int, y: Int, replacementColor: Int, tolerance: Int = 30) {
        val width = bitmap.width
        val height = bitmap.height

        if (x !in 0 until width || y !in 0 until height) return

        val targetColor = bitmap.getPixel(x, y)
        if (areColorsSimilar(targetColor, replacementColor, tolerance)) return

        val queue = ArrayDeque<Pair<Int, Int>>()
        val visited = HashSet<Pair<Int, Int>>()
        queue.add(Pair(x, y))

        val paint = Paint().apply {
            color = replacementColor
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        while (queue.isNotEmpty()) {
            val (px, py) = queue.removeFirst()
            if (px !in 0 until width || py !in 0 until height) continue
            if (!visited.add(Pair(px, py))) continue

            if (visited.size > width * height * 0.01 * drawPaint.strokeWidth ) {
                Log.w("FloodFill", "Flood fill aborted to prevent OOM")
                break
            }

            val currentColor = bitmap.getPixel(px, py)
            if (currentColor == Color.BLACK) continue

            if (!areColorsSimilar(currentColor, targetColor, tolerance)) continue

            // Dùng canvas vẽ thay vì setPixel
            drawingCanvas?.drawRect(
                px.toFloat(), py.toFloat(), px + 1f, py + 1f, paint
            )

            if (isBlingMode && Math.random() < 0.002) {
                drawingCanvas?.drawCircle(
                    px.toFloat(),
                    py.toFloat(),
                    1.5f + Math.random().toFloat() * 1.5f,
                    Paint().apply {
                        color = Color.WHITE
                        style = Paint.Style.FILL
                    }
                )
            }

            queue.add(Pair(px + 1, py))
            queue.add(Pair(px - 1, py))
            queue.add(Pair(px, py + 1))
            queue.add(Pair(px, py - 1))
        }

        invalidate()
    }
    private fun areColorsSimilar(color1: Int, color2: Int, tolerance: Int): Boolean {
        if (Color.alpha(color1) == 0) return true

        val dr = Color.red(color1) - Color.red(color2)
        val dg = Color.green(color1) - Color.green(color2)
        val db = Color.blue(color1) - Color.blue(color2)

        return dr * dr + dg * dg + db * db <= tolerance * tolerance
    }

    private fun saveToUndoStack() {
        drawingBitmap?.let {
            val snapshot = it.copy(Bitmap.Config.ARGB_8888, true)
            undoStack.push(snapshot)
            redoStack.clear() // Mỗi lần hành động mới, redoStack bị xóa
            updateUndoRedoCounts()
        }
    }

    fun undo() {
        if (undoStack.size > 1) { // giữ lại ít nhất 1 trạng thái gốc
            redoStack.push(drawingBitmap!!.copy(Bitmap.Config.ARGB_8888, true))
            undoStack.pop()
            drawingBitmap = undoStack.peek().copy(Bitmap.Config.ARGB_8888, true)
            drawingCanvas = Canvas(drawingBitmap!!)
            updateUndoRedoCounts()
            invalidate()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.push(drawingBitmap!!.copy(Bitmap.Config.ARGB_8888, true))
            drawingBitmap = redoStack.pop().copy(Bitmap.Config.ARGB_8888, true)
            drawingCanvas = Canvas(drawingBitmap!!)
            updateUndoRedoCounts()
            invalidate()
        }
    }


    fun resetZoomAndPan() {
        scaleFactor = 1f
        posX = 0f
        posY = 0f
        invalidate()
    }

    private fun isNearBlack(color: Int, tolerance: Int = 50): Boolean {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return r * r + g * g + b * b <= tolerance * tolerance
    }

    private fun isOutOfBounds(): Boolean {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val contentWidth = drawingBitmap?.width?.times(scaleFactor) ?: 0f
        val contentHeight = drawingBitmap?.height?.times(scaleFactor) ?: 0f

        val left = posX
        val top = posY
        val right = left + contentWidth
        val bottom = top + contentHeight

        return (right < 0 || left > viewWidth || bottom < 0 || top > viewHeight)
    }

    private fun updateUndoRedoCounts() {
        undoCount = undoStack.size
        redoCount = redoStack.size
        notifyUndoRedoCount()
    }


    var onUndoRedoCountChanged: ((undoCount: Int, redoCount: Int) -> Unit)? = null

    private fun notifyUndoRedoCount() {
        onUndoRedoCountChanged?.invoke(undoCount, redoCount)
    }

}



