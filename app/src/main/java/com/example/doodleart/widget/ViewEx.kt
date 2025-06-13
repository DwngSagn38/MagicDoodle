package com.example.doodleart.widget


import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.doodleart.R
import java.io.File
import java.io.FileOutputStream


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


fun savePaintViewToFile(view: View, context: Context): String {
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)

    val filename = "paint_${System.currentTimeMillis()}.png"
    val file = File(context.filesDir, filename)
    val fos = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    fos.flush()
    fos.close()

    return file.absolutePath
}

fun saveBitmapToGallery(bitmap: Bitmap,  context: Context) {
    val filename = "mandala_${System.currentTimeMillis()}.png"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        val outputStream = resolver.openOutputStream(it)
        outputStream.use { stream ->
            if (stream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
        }
    }
}





