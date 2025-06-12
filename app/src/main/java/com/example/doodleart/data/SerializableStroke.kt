package com.example.doodleart.data
data class SerializableStroke(
    val points: List<MyPointF>,
    val color: Int,
    val effectName: String,
    val symmetryCount: Int,
    val gradientColors: IntArray? = null,
    val strokeWidth: Float = 4f,
    val shape: StrokeShape = StrokeShape.NORMAL,
    val drawableResId: Int? = null
)
