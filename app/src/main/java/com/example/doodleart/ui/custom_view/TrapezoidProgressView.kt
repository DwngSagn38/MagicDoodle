package com.example.doodleart.ui.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class TrapezoidProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var progress: Float = 0.3f // từ 0f đến 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
            invalidate()
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val centerY = viewHeight / 2f
        val radius = viewHeight / 2f

        val progressWidth = viewWidth * progress

        // Độ cao thuôn hình thang
        val topStart = 6f
        val topEnd = 14f
        val bottomStart = 12f
        val bottomEnd = viewHeight

        val topHeight = topStart + (topEnd - topStart) * progress
        val bottomHeight = bottomStart + (bottomEnd - bottomStart) * progress

        // === 1. NỀN ===
        paint.shader = null
        paint.color = Color.parseColor("#213773")

        val bgPath = Path().apply {
            moveTo(0f, centerY - bottomStart / 2f)
            lineTo(viewWidth, centerY - bottomEnd / 2f)
            lineTo(viewWidth, centerY + bottomEnd / 2f)
            lineTo(0f, centerY + bottomStart / 2f)
            close()
        }

        canvas.save()
        val bgClip = Path().apply {
            addRoundRect(0f, 0f, viewWidth, viewHeight, radius, radius, Path.Direction.CW)
        }
        canvas.clipPath(bgClip)
        canvas.drawPath(bgPath, paint)
        canvas.restore()

        // === 2. PROGRESS ===
        val shader = LinearGradient(
            0f, 0f, progressWidth, 0f,
            Color.parseColor("#FFE500"),
            Color.parseColor("#77D93E"),
            Shader.TileMode.CLAMP
        )
        paint.shader = shader

        val fgPath = Path().apply {
            moveTo(0f, centerY - bottomStart / 2f)
            lineTo(progressWidth, centerY - bottomHeight / 2f)
            lineTo(progressWidth, centerY + bottomHeight / 2f)
            lineTo(0f, centerY + bottomStart / 2f)
            close()
        }

        canvas.save()
        val fgClip = Path().apply {
            addRoundRect(0f, 0f, progressWidth, viewHeight, radius, radius, Path.Direction.CW)
        }
        canvas.clipPath(fgClip)
        canvas.drawPath(fgPath, paint)
        canvas.restore()
    }
}

