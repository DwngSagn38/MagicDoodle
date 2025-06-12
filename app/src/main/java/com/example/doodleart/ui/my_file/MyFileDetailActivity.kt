package com.example.doodleart.ui.my_file

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.doodleart.R
import com.example.doodleart.base.BaseActivity
import com.example.doodleart.databinding.ActivityMyFileDetailBinding
import com.example.doodleart.roomdb.DBHelper
import com.example.doodleart.ui.my_file.fragment.MyFileAdapter
import kotlinx.coroutines.launch

class MyFileDetailActivity : BaseActivity<ActivityMyFileDetailBinding>() {

    override fun setViewBinding(): ActivityMyFileDetailBinding {
        return ActivityMyFileDetailBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val fileId = intent.getIntExtra("fileId", 0)
        lifecycleScope.launch {
            val db = DBHelper.getDatabase(this@MyFileDetailActivity)
            val myfile = db.fileDao().getFileById(fileId)

            val bitmap = BitmapFactory.decodeFile(myfile!!.path)
            binding.imgMyFile.setImageBitmap(bitmap)
        }
    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }

}