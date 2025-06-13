package com.example.doodleart.ui.coloring

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.doodleart.R
import com.example.doodleart.base.BaseActivity
import com.example.doodleart.data.DataApp
import com.example.doodleart.databinding.ActivityColoringDetailBinding
import com.example.doodleart.ui.coloring.drawing.ColorDrawingActivity
import com.example.doodleart.widget.saveBitmapToGallery
import com.example.doodleart.widget.tap
import java.io.File
import java.io.FileOutputStream

class ColoringDetailActivity : BaseActivity<ActivityColoringDetailBinding>() {
    private var idColoring : Int = 0
    override fun setViewBinding(): ActivityColoringDetailBinding {
        return ActivityColoringDetailBinding.inflate(layoutInflater)
    }

    override fun initView() {
        idColoring = intent.getIntExtra("id", 0)
        binding.imgColoring.setImageResource(DataApp.getListColoring()[idColoring].img)
        binding.imgBack.tap { finish() }
        binding.tvDrawNow.tap {
            val intent = Intent(this, ColorDrawingActivity::class.java)
            intent.putExtra("id", idColoring)
            startActivity(intent)
            finish()
        }
    }

    override fun viewListener() {
        binding.apply {
            imgSave.tap { DownFile() }
            imgShare.tap { shareViewAsImage(binding.imgColoring) }
        }
    }

    override fun dataObservable() {
    }

    private fun DownFile(){
        val resId = DataApp.getListColoring()[idColoring].img
        val bitmap = BitmapFactory.decodeResource(resources, resId)

        saveBitmapToGallery(bitmap, this@ColoringDetailActivity)
        val mess = getString(R.string.save_to_gallery)
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show()
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
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