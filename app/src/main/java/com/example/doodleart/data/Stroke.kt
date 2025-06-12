package com.example.doodleart.data

import android.graphics.PointF
import com.example.doodleart.ui.custom_view.MandalaDrawView

enum class StrokeShape {
    NORMAL, CIRCLE, HEART, STAR, SQUARE, TRIANGLE, HEXAGON, SMILEY_FACE, IMAGE,
    STAR4, PLUS, ELLIPSE, DASHED
}

data class Stroke(
    val points: MutableList<PointF>,
    val color: Int,
    val effect: MandalaDrawView.StrokeEffect,
    val symmetryCount: Int,
    val gradientColors: IntArray? = null,
    val strokeWidth: Float = 4f,
    val shape: StrokeShape = StrokeShape.NORMAL,
    val drawableResId: Int? = null
)
