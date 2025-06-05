package com.example.doodleart.ui.custom_view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.example.doodleart.R
import com.example.doodleart.data.Stroke
import com.example.doodleart.data.StrokeShape

class MandalaDrawView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val strokes = mutableListOf<Stroke>()
    private var currentStroke: Stroke? = null
    private var currentShape: StrokeShape = StrokeShape.NORMAL
    var shapeSpacing: Float = 50f
    private var lastDrawnPoint: PointF? = null
    private val undoneStrokes = mutableListOf<Stroke>()
    private var isEraserOn: Boolean = false
    private var selectedColor: Int = Color.BLACK
    private var backgroundColor: Int = Color.WHITE

    private var centerX = 0f
    private var centerY = 0f
    private var previousStrokeWidth: Float = 5f

    private var currentStrokeWidth = 4f // CHỈ ÁP DỤNG CHO NÉT VẼ MỚI
    private var currentColor: Int = Color.BLACK
    private var currentEffect: StrokeEffect = StrokeEffect.NORMAL
    private var currentGradientColors: IntArray? = null
    private var currentSymmetryCount: Int = 1
    private var img: Int = R.drawable.icon1

    enum class StrokeEffect {
        NORMAL, GLOW, GRADIENT, GLOW_GRADIENT, SMOKY
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                undoneStrokes.clear()
                val strokeColor = if (isEraserOn) Color.WHITE else currentColor
                val current = if (isEraserOn) 1 else currentSymmetryCount

                currentStroke = Stroke(
                    points = mutableListOf(PointF(x, y)),
                    color = strokeColor,
                    effect = currentEffect,
                    symmetryCount = current,
                    gradientColors = if (currentEffect == StrokeEffect.GRADIENT || currentEffect == StrokeEffect.GLOW_GRADIENT) currentGradientColors else null,
                    strokeWidth = currentStrokeWidth,
                    shape = currentShape
                )
                strokes.add(currentStroke!!)
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                if (currentShape == StrokeShape.NORMAL) {
                    currentStroke?.points?.add(PointF(x, y))
                } else {
                    val currentPoint = PointF(x, y)
                    val shouldDraw = lastDrawnPoint == null ||
                            distanceBetween(lastDrawnPoint!!, currentPoint) >= shapeSpacing

                    if (shouldDraw) {
                        val shapeStroke = Stroke(
                            points = mutableListOf(currentPoint),
                            color = currentColor,
                            effect = currentEffect,
                            symmetryCount = currentSymmetryCount,
                            gradientColors = if (currentEffect == StrokeEffect.GRADIENT || currentEffect == StrokeEffect.GLOW_GRADIENT) currentGradientColors else null,
                            strokeWidth = currentStrokeWidth,
                            shape = currentShape
                        )
                        strokes.add(shapeStroke)
                        lastDrawnPoint = currentPoint
                    }
                }
                invalidate()
            }



            MotionEvent.ACTION_UP -> {
                currentStroke = null
                invalidate()
            }
        }
        return true
    }
    private fun distanceBetween(p1: PointF, p2: PointF): Float {
        val dx = p1.x - p2.x
        val dy = p1.y - p2.y
        return Math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(backgroundColor)

        canvas.save()
        canvas.translate(centerX, centerY)

        for (stroke in strokes) {
            drawStroke(canvas, stroke)
        }

        canvas.restore()
    }

    fun setBackgroundColorCustom(color: Int) {
        backgroundColor = color
        invalidate()
    }

    fun setEraser(enabled: Boolean) {
        isEraserOn = enabled
        if (enabled) {
            previousStrokeWidth = currentStrokeWidth
            currentStrokeWidth = 20f
            currentColor = Color.WHITE
        } else {
            // Trở lại màu và nét ban đầu
            currentStrokeWidth = previousStrokeWidth
            currentColor = selectedColor // cần lưu màu đang chọn trước đó
        }
        invalidate()
    }


    fun isEraserOn(): Boolean {
        return isEraserOn
    }

    private fun drawStroke(canvas: Canvas, stroke: Stroke) {
        val path = Path()

        if (stroke.shape == StrokeShape.NORMAL && stroke.points.isNotEmpty()) {
            val firstPoint = stroke.points[0]
            path.moveTo(firstPoint.x - centerX, firstPoint.y - centerY)
            for (i in 1 until stroke.points.size) {
                val point = stroke.points[i]
                path.lineTo(point.x - centerX, point.y - centerY)
            }
        } else if (stroke.shape != StrokeShape.NORMAL && stroke.points.isNotEmpty()) {
            // Phần vẽ các hình dạng đặc biệt (circle, star, heart, ...)
            val symmetry = stroke.symmetryCount.coerceAtLeast(1)
            for (point in stroke.points) {
                for (i in 0 until symmetry) {
                    canvas.save()
                    val angle = 360f / symmetry * i
                    canvas.rotate(angle)
                    canvas.translate(point.x - centerX, point.y - centerY)

                    when (stroke.shape) {
                        StrokeShape.CIRCLE -> canvas.drawCircle(0f, 0f, stroke.strokeWidth, Paint().apply {
                            color = stroke.color
                            style = Paint.Style.FILL
                            isAntiAlias = true
                        })
                        StrokeShape.HEART -> drawHeart(canvas, stroke)
                        StrokeShape.STAR -> drawStar(canvas, stroke)
                        StrokeShape.SQUARE -> drawSquare(canvas, stroke)
                        StrokeShape.TRIANGLE -> drawTriangle(canvas, stroke)
                        StrokeShape.HEXAGON -> drawHexagon(canvas, stroke)
                        StrokeShape.SMILEY_FACE -> drawSmiley(canvas, stroke)
                        StrokeShape.IMAGE -> drawImg(canvas, stroke,context)
                        else -> {}
                    }

                    canvas.restore()
                }
            }
            return
        }

        // Các paint bạn đã tạo
        val paintNormal = Paint().apply {
            color = stroke.color
            strokeWidth = stroke.strokeWidth
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val paintGlow = Paint(paintNormal).apply {
            color = ColorUtils.blendARGB(stroke.color, Color.WHITE, 0.4f)
            strokeWidth = stroke.strokeWidth * 2
            alpha = 220
            maskFilter = BlurMaskFilter(stroke.strokeWidth, BlurMaskFilter.Blur.NORMAL)
        }

        val paintGradient = Paint(paintNormal)

        val symmetry = stroke.symmetryCount.coerceAtLeast(1)
        for (i in 0 until symmetry) {
            canvas.save()
            val angle = 360f / symmetry * i
            canvas.rotate(angle)

            when (stroke.effect) {
                StrokeEffect.NORMAL -> canvas.drawPath(path, paintNormal)

                StrokeEffect.GLOW -> {
                    canvas.drawPath(path, paintGlow)
                    canvas.drawPath(path, paintNormal)
                }

                StrokeEffect.GRADIENT -> {
                    drawGradient(canvas, path, paintGradient, stroke.gradientColors, stroke.strokeWidth)
                }

                StrokeEffect.GLOW_GRADIENT -> {
                    canvas.drawPath(path, paintGlow)
                    drawGradient(canvas, path, paintGradient, stroke.gradientColors, stroke.strokeWidth)
                }
                StrokeEffect.SMOKY -> {
                    val smokyPaint = Paint(paintNormal).apply {
                        maskFilter = BlurMaskFilter(stroke.strokeWidth * 1.5f, BlurMaskFilter.Blur.NORMAL)
                        alpha = 150
                    }
                    canvas.drawPath(path, smokyPaint)
                }
            }

            canvas.restore()
        }
    }
    fun undo() {
        if (strokes.isNotEmpty()) {
            val lastStroke = strokes.removeAt(strokes.size - 1)
            undoneStrokes.add(lastStroke)
            invalidate()
        }
    }

    fun redo() {
        if (undoneStrokes.isNotEmpty()) {
            val stroke = undoneStrokes.removeAt(undoneStrokes.size - 1)
            strokes.add(stroke)
            invalidate()
        }
    }

    private fun drawHeart(canvas: Canvas, stroke: Stroke) {
        val paint = Paint().apply {
            color = stroke.color
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val path = Path()
        val size = stroke.strokeWidth * 2

        path.moveTo(0f, -size / 4)
        path.cubicTo(size / 2, -size * 3 / 4, size * 3 / 2, size / 4, 0f, size)
        path.cubicTo(-size * 3 / 2, size / 4, -size / 2, -size * 3 / 4, 0f, -size / 4)
        canvas.drawPath(path, paint)
    }
    private fun drawSquare(canvas: Canvas, stroke: Stroke) {
        val size = stroke.strokeWidth * 2
        val half = size / 2
        val paint = Paint().apply {
            color = stroke.color
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRect(-half, -half, half, half, paint)
    }

    private fun drawTriangle(canvas: Canvas, stroke: Stroke) {
        val size = stroke.strokeWidth * 2
        val path = Path()
        path.moveTo(0f, -size)
        path.lineTo(size, size)
        path.lineTo(-size, size)
        path.close()

        val paint = Paint().apply {
            color = stroke.color
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawPath(path, paint)
    }

    private fun drawHexagon(canvas: Canvas, stroke: Stroke) {
        val radius = stroke.strokeWidth * 2
        val path = Path()
        for (i in 0..5) {
            val angle = Math.toRadians((60 * i - 30).toDouble())
            val x = (radius * Math.cos(angle)).toFloat()
            val y = (radius * Math.sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()

        val paint = Paint().apply {
            color = stroke.color
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawPath(path, paint)
    }

    private fun drawSmiley(canvas: Canvas, stroke: Stroke) {
        val radius = stroke.strokeWidth * 2
        val paint = Paint().apply {
            color = stroke.color
            style = Paint.Style.STROKE
            strokeWidth = stroke.strokeWidth
            isAntiAlias = true
        }

        // Mặt
        canvas.drawCircle(0f, 0f, radius, paint)

        // Mắt
        canvas.drawCircle(-radius / 2.5f, -radius / 3f, radius / 5f, paint)
        canvas.drawCircle(radius / 2.5f, -radius / 3f, radius / 5f, paint)

        // Miệng
        val mouth = RectF(-radius / 1.5f, -radius / 5f, radius / 1.5f, radius / 1.2f)
        canvas.drawArc(mouth, 20f, 140f, false, paint)
    }



    private fun drawImg(canvas: Canvas, stroke: Stroke, context: Context) {
        val radius = stroke.strokeWidth *4
        val paint = Paint().apply {
            color = stroke.color
            style = Paint.Style.STROKE
            strokeWidth = stroke.strokeWidth
            isAntiAlias = true
        }

        canvas.save()

        val drawable = ContextCompat.getDrawable(context, R.drawable.icon1)
        drawable?.let {
            val size = radius.toInt() * 2
            val bitmap = drawableToBitmap(it, size, size)

            val left = -size / 2f
            val top = -size / 2f
            canvas.drawBitmap(bitmap, left, top, null)
        }

        canvas.restore()
    }


    fun drawableToBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun drawStar(canvas: Canvas, stroke: Stroke) {
        val paint = Paint().apply {
            color = stroke.color
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val path = Path()
        val outerRadius = stroke.strokeWidth * 1.5f
        val innerRadius = outerRadius / 2.5f
        val angle = Math.PI / 5

        for (i in 0 until 10) {
            val r = if (i % 2 == 0) outerRadius else innerRadius
            val x = (r * Math.sin(i * angle)).toFloat()
            val y = -(r * Math.cos(i * angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawGradient(canvas: Canvas, path: Path, paint: Paint, colors: IntArray?, width: Float) {
        if (colors == null || colors.size < 2) return

        val (start, end) = getPathStartEnd(path)
        paint.shader = LinearGradient(
            start.x, start.y, end.x, end.y,
            colors, null, Shader.TileMode.CLAMP
        )
        paint.strokeWidth = width
        canvas.drawPath(path, paint)
    }

    private fun drawStar(path: Path, cx: Float, cy: Float, radius: Float) {
        val angle = Math.PI / 5
        for (i in 0..9) {
            val r = if (i % 2 == 0) radius else radius / 2
            val x = (cx + r * Math.cos(i * angle - Math.PI / 2)).toFloat()
            val y = (cy + r * Math.sin(i * angle - Math.PI / 2)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
    }

    private fun getPathStartEnd(path: Path): Pair<PointF, PointF> {
        val pathMeasure = PathMeasure(path, false)
        val start = FloatArray(2)
        val end = FloatArray(2)

        pathMeasure.getPosTan(0f, start, null)
        pathMeasure.getPosTan(pathMeasure.length, end, null)

        return PointF(start[0], start[1]) to PointF(end[0], end[1])
    }

    // ==== Public Setter Methods ====

    fun setStrokeColor(color: Int) {
        currentColor = color
    }

    fun setStrokeEffect(effect: StrokeEffect) {
        currentEffect = effect
    }

    fun getStrokeEffect(): StrokeEffect = currentEffect

    fun setGradientColors(colors: IntArray) {
        if (colors.size >= 2) {
            currentGradientColors = colors
        }
    }

    fun setSymmetryCount(count: Int) {
        currentSymmetryCount = count
    }

    fun setImgPen(count: Int) {
        img = count
    }

    fun setStrokeWidth(width: Float) {
        currentStrokeWidth = width // chỉ áp dụng cho nét vẽ sau
    }

    fun clearCanvas() {
        strokes.clear()
        invalidate()
    }
    fun setStrokeShape(shape: StrokeShape) {
        currentShape = shape
    }

}

