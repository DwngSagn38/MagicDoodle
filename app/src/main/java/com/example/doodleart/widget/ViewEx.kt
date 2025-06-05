package com.example.doodleart.widget


import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.doodleart.R


fun View.tap(action: (view: View?) -> Unit) {
    setOnClickListener {
        it.isEnabled = false
        it.postDelayed({ it.isEnabled = true }, 1500)
        action(it)
    }
}


fun View.tapin(action: (view: View?) -> Unit) {
    setOnClickListener {
        it.isEnabled = false
        it.postDelayed({ it.isEnabled = true }, 10)
        action(it)
    }
}

fun View.tapRotate(action: (view: View?) -> Unit) {
    setOnClickListener {
        it.isEnabled = false
        it.postDelayed({ it.isEnabled = true }, 500)
        action(it)
    }
}


fun View.visible() {
    visibility = View.VISIBLE // hiện view
}

fun View.gone() {
    visibility = View.GONE // ẩn view
}

fun View.invisible() {
    visibility = View.INVISIBLE
}
fun View.onAvoidDoubleClick(
    throttleDelay: Long = 600,
    onClick: (View) -> Unit
) {
    tap {
        onClick(this)
        isClickable = false
        postDelayed({ isClickable = true }, throttleDelay)
    }
}

private var lastClick = 0L
fun <T : View> T.onClick(delayBetweenClick: Long = 0, block: T.() -> Unit) {
    tap {
        when {
            delayBetweenClick <= 0 -> {
                block()
            }

            System.currentTimeMillis() - lastClick > delayBetweenClick -> {
                lastClick = System.currentTimeMillis()
                block()
            }

            else -> {

            }
        }
    }
}

fun TextView.setGradientText(context : Context) {
    val shader = LinearGradient(
        0f, 0f, width.toFloat(), textSize,
        ContextCompat.getColor(context, R.color.primary_1),
        ContextCompat.getColor(context, R.color.primary_2),
        Shader.TileMode.CLAMP
    )
    paint.shader = shader
}



