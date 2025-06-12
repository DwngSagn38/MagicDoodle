package com.example.doodleart.ui.main

import android.content.Intent
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.doodleart.R
import com.example.doodleart.base.BaseActivity
import com.example.doodleart.databinding.ActivityMainBinding
import com.example.doodleart.ui.free_creation.FreeCreationActivity
import com.example.doodleart.widget.tap
import com.example.doodleart.ui.coloring.ColoringActivity
import com.example.doodleart.ui.my_file.MyFileActivity
import com.example.doodleart.ui.setting.SettingActivity
import com.example.doodleart.utils.showColorPicker
import com.example.doodleart.widget.tap

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun setViewBinding(): ActivityMainBinding {
        return  ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {

        binding.btnSetting.tap { showActivity(SettingActivity::class.java) }

    }

    override fun viewListener() {
        binding.tvFreeCreation.tap {
            val intent = Intent(this, FreeCreationActivity::class.java)
            intent.putExtra("aspect_ratio", "9:16") // Hoặc "1:1", "4:5"
            startActivity(intent)
        }
        binding.tvColoring.tap { showActivity(ColoringActivity::class.java) }
        binding.tvMyFile.tap { showActivity(MyFileActivity::class.java) }
    }

    override fun dataObservable() {
    }

}