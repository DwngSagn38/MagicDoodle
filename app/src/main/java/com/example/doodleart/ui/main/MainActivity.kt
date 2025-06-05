package com.example.doodleart.ui.main

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

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun setViewBinding(): ActivityMainBinding {
        return  ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val blurEffect = RenderEffect.createBlurEffect(30f, 30f, Shader.TileMode.CLAMP)
            binding.cvMyfile.setRenderEffect(blurEffect)
        }
    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }

}