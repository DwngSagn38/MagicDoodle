package com.doodleart.paintcolor.drawart.ui.my_file

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.doodleart.paintcolor.drawart.R
import com.doodleart.paintcolor.drawart.base.BaseActivity
import com.doodleart.paintcolor.drawart.data.DataApp
import com.doodleart.paintcolor.drawart.databinding.ActivityMyFileDetailBinding
import com.doodleart.paintcolor.drawart.dialog.DeleteDialog
import com.doodleart.paintcolor.drawart.model.MyFileModel
import com.doodleart.paintcolor.drawart.roomdb.DBHelper
import com.doodleart.paintcolor.drawart.ui.coloring.drawing.ColorDrawingActivity
import com.doodleart.paintcolor.drawart.ui.free_creation.FreeCreationActivity
import com.doodleart.paintcolor.drawart.ui.main.MainActivity
import com.doodleart.paintcolor.drawart.ui.my_file.fragment.MyFileAdapter
import com.doodleart.paintcolor.drawart.widget.gone
import com.doodleart.paintcolor.drawart.widget.invisible
import com.doodleart.paintcolor.drawart.widget.saveBitmapToGallery
import com.doodleart.paintcolor.drawart.widget.savePaintViewToFile
import com.doodleart.paintcolor.drawart.widget.tap
import com.doodleart.paintcolor.drawart.widget.visible
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MyFileDetailActivity : BaseActivity<ActivityMyFileDetailBinding>() {

    private lateinit var myfile: MyFileModel
    private var checkVisible: Boolean = false

    override fun setViewBinding(): ActivityMyFileDetailBinding {
        return ActivityMyFileDetailBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val fileId = intent.getIntExtra("fileId", 0)
        checkVisible = intent.getBooleanExtra("checkVisible", false)
        lifecycleScope.launch {
            val db = DBHelper.getDatabase(this@MyFileDetailActivity)
            myfile = db.fileDao().getFileById(fileId)!!

            val bitmap = BitmapFactory.decodeFile(myfile!!.path)
            setImg(bitmap, checkVisible)
        }

        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.imgHome.setOnClickListener {
            showActivity(MainActivity::class.java)
            finishAffinity()
        }
    }

    override fun viewListener() {

        binding.apply {
            imgDelete.tap { showDialogDelete() }
            imgDown.tap { DownFile() }
            imgShare.tap { shareViewAsImage(binding.imgMyFile) }
            imgEdit.tap {
                if (checkVisible) {
                    intent = Intent(this@MyFileDetailActivity, FreeCreationActivity::class.java)
                    intent.putExtra("bitmap", myfile.path)
                    intent.putExtra("aspect_ratio", myfile.typeSize)
                    intent.putExtra("edit", true)
                    startActivity(intent)
                    finish()
                } else {
                    intent = Intent(this@MyFileDetailActivity, ColorDrawingActivity::class.java)
                    intent.putExtra("id", myfile.id)
                    intent.putExtra("edit", true)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun setImg(bitmap:Bitmap,checkVisible: Boolean) {
        binding.imgMyFile.setImageBitmap(bitmap)
        Log.d("TAG", "setImg: $checkVisible")
        if (checkVisible) {
            binding.apply {
                imgMyFileColorating.gone()
                imgMyFileDraw.visible()
                imgMyFileDraw.loadImage(bitmap,true)
                imgMyFileDraw.setPreviewMode(true)
            }

        } else {
            binding.apply {
                imgMyFileColorating.visible()
                imgMyFileDraw.gone()
                imgMyFileColorating.loadImage(bitmap,true)
                imgMyFileColorating.setPreviewMode(true)
            }
        }

    }

    override fun dataObservable() {
    }

    private fun showDialogDelete(){
        val button = getString(R.string.yes)
        val dialog = DeleteDialog(this,
            mess = getString(R.string.are_you_delete_it),
            content = button,
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

    private fun DownFile() {
        val bitmap = BitmapFactory.decodeFile(myfile!!.path)
        saveBitmapToGallery(bitmap, this@MyFileDetailActivity)
        val mess = getString(R.string.photo_saved_to_device)
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