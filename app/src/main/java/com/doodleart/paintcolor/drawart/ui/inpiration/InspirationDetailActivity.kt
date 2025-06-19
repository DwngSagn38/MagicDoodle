package com.example.doodleart.ui.inpiration

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.doodleart.R
import com.example.doodleart.base.BaseActivity
import com.example.doodleart.data.DataApp
import com.example.doodleart.databinding.ActivityColoringDetailBinding
import com.example.doodleart.databinding.ActivityInspirationDetailBinding
import com.example.doodleart.ui.coloring.drawing.ColorDrawingActivity
import com.example.doodleart.ui.free_creation.FreeCreationActivity
import com.example.doodleart.widget.tap
import java.io.File
import java.io.FileOutputStream

class InspirationDetailActivity : BaseActivity<ActivityInspirationDetailBinding>() {
    private var idColoring : Int = 0

    override fun setViewBinding(): ActivityInspirationDetailBinding {
        return ActivityInspirationDetailBinding.inflate(layoutInflater)
    }

    override fun initView() {
        idColoring = intent.getIntExtra("id", 0)

        binding.imgColoring.setImageResource(DataApp.getListInpiration()[idColoring].img)
        binding.imgBack.tap { finish() }
        binding.tvDrawNow.tap {
            val intent = Intent(this, FreeCreationActivity::class.java)
            intent.putExtra("id", idColoring)
            intent.putExtra("aspect_ratio", "4:5")

            startActivity(intent)
            finish()
        }
        binding.imgSave.tap {
            saveImg()
        }
    }
    private fun saveImg() {
        val cardView = findViewById<ConstraintLayout>(R.id.clCenter)
        cardView?.let {
            val bitmap = getBitmapFromView(it)
            saveBitmapToGallery(bitmap)
            Toast.makeText(this, getString(R.string.photo_saved_to_device), Toast.LENGTH_SHORT)
                .show()
        }
    }
    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        view.draw(canvas)
        return returnedBitmap
    }
    private fun saveBitmapToGallery(bitmap: Bitmap) {
        val filename = "mandala_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val resolver = applicationContext.contentResolver
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


    override fun viewListener() {
        binding.imgShare.tap { shareViewAsImage(binding.imgColoring) }
    }

    override fun dataObservable() {
    }

    private fun shareViewAsImage(view: View) {
        val bitmap = getBitmapFromView(view)

        // Tạo file tạm
        val file = File(cacheDir, "shared_view.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "Chia sẻ ảnh"))
    }
}