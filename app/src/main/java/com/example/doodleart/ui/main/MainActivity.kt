package com.example.doodleart.ui.main

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
import com.example.doodleart.ui.setting.SettingActivity
import com.example.doodleart.utils.showColorPicker
import com.example.doodleart.widget.tap

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private var currentColorInt: Int = Color.WHITE

    override fun setViewBinding(): ActivityMainBinding {
        return  ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val blurEffect = RenderEffect.createBlurEffect(30f, 30f, Shader.TileMode.CLAMP)
            binding.cvMyfile.setRenderEffect(blurEffect)
        }

        binding.btnSetting.tap { showActivity(SettingActivity::class.java) }

    }

    override fun viewListener() {
        binding.tvFreeCreation.tap {
            showActivity(FreeCreationActivity::class.java)
        }
        binding.tvColoring.tap { showActivity(ColoringActivity::class.java) }
        binding.cvMyfile.tap {
            showColorPicker(currentColorInt,
                onColorPicked = { colorString ->
                    currentColorInt = Color.parseColor(colorString)
                    binding.tvMy.text = "Màu: $colorString"
                    binding.tvMy.setBackgroundColor(currentColorInt)
                },
                onDismiss = { }
            )
        }
    }


    override fun dataObservable() {
    }

}