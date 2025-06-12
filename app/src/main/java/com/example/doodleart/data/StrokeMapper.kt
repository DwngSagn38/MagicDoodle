package com.example.doodleart.data

import android.graphics.PointF
import com.example.doodleart.ui.custom_view.MandalaDrawView

fun Stroke.toSerializable(): SerializableStroke {
    return SerializableStroke(
        points = this.points.map { MyPointF(it.x, it.y) },
        color = this.color,
        effectName = this.effect.name,
        symmetryCount = this.symmetryCount,
        gradientColors = this.gradientColors,
        strokeWidth = this.strokeWidth,
        shape = this.shape,
        drawableResId = this.drawableResId
    )
}

fun SerializableStroke.toStroke(): Stroke {
    return Stroke(
        points = this.points.map { PointF(it.x, it.y) }.toMutableList(),
        color = this.color,
        effect = MandalaDrawView.StrokeEffect.valueOf(this.effectName),
        symmetryCount = this.symmetryCount,
        gradientColors = this.gradientColors,
        strokeWidth = this.strokeWidth,
        shape = this.shape,
        drawableResId = this.drawableResId
    )
}
