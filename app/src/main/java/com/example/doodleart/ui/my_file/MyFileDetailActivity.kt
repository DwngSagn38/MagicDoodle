package com.example.doodleart.ui.my_file

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.doodleart.R
import com.example.doodleart.base.BaseActivity
import com.example.doodleart.data.DataApp
import com.example.doodleart.databinding.ActivityMyFileDetailBinding
import com.example.doodleart.dialog.DeleteDialog
import com.example.doodleart.model.MyFileModel
import com.example.doodleart.roomdb.DBHelper
import com.example.doodleart.ui.coloring.drawing.ColorDrawingActivity
import com.example.doodleart.ui.main.MainActivity
import com.example.doodleart.ui.my_file.fragment.MyFileAdapter
import com.example.doodleart.widget.gone
import com.example.doodleart.widget.invisible
import com.example.doodleart.widget.saveBitmapToGallery
import com.example.doodleart.widget.savePaintViewToFile
import com.example.doodleart.widget.tap
import com.example.doodleart.widget.visible
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MyFileDetailActivity : BaseActivity<ActivityMyFileDetailBinding>() {

    private lateinit var myfile : MyFileModel

    override fun setViewBinding(): ActivityMyFileDetailBinding {
        return ActivityMyFileDetailBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val fileId = intent.getIntExtra("fileId", 0)
        val checkVisible = intent.getBooleanExtra("checkVisible", false)
        if (checkVisible) {
            binding.imgEdit.gone()
        } else {
            binding.imgEdit.visible()

        }
        lifecycleScope.launch {
            val db = DBHelper.getDatabase(this@MyFileDetailActivity)
            myfile = db.fileDao().getFileById(fileId)!!

            val bitmap = BitmapFactory.decodeFile(myfile!!.path)
            setImg(bitmap,checkVisible)        }

        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.imgHome.setOnClickListener { showActivity(MainActivity::class.java) }
    }

    override fun viewListener() {
        binding.apply {
            imgDelete.tap { showDialogDelete() }
            imgDown.tap { DownFile() }
            imgShare.tap { shareViewAsImage(binding.imgMyFileDraw) }
            imgEdit.tap {
                intent = Intent(this@MyFileDetailActivity, ColorDrawingActivity::class.java)
                intent.putExtra("id", myfile.id)
                intent.putExtra("edit",true)
                startActivity(intent)
                finish()
            }
        }
    }
    private fun setImg(bitmap:Bitmap,checkVisible: Boolean) {
        if (checkVisible) {
            binding.imgMyFileColorating.visible()
            binding.imgMyFileDraw.gone()
            binding.imgMyFileColorating.setImageBitmap(bitmap)
        } else {
            binding.imgMyFileColorating.gone()
            binding.imgMyFileDraw.visible()
            binding.imgMyFileDraw.setImageBitmap(bitmap)
        }

    }

    override fun dataObservable() {
    }

    private fun showDialogDelete(){
        val dialog = DeleteDialog(this,
            mess = getString(R.string.are_you_delete_it),
            action = {
                lifecycleScope.launch {
                    val db = DBHelper.getDatabase(this@MyFileDetailActivity)
                    db.fileDao().deleteFile(myfile)
                }
                finish()
            },
            no = {})
        dialog.show()
    }

    private fun DownFile(){
        val bitmap = BitmapFactory.decodeFile(myfile!!.path)
        val path = saveBitmapToGallery(bitmap, this@MyFileDetailActivity)
        Toast.makeText(this, "Đã lưu ảnh vào ${path}", Toast.LENGTH_SHORT).show()
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