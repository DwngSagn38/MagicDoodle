package com.example.doodleart.ui.coloring

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.doodleart.R
import com.example.doodleart.base.BaseActivity
import com.example.doodleart.data.DataApp
import com.example.doodleart.databinding.ActivityColoringDetailBinding
import com.example.doodleart.widget.tap

class ColoringDetailActivity : BaseActivity<ActivityColoringDetailBinding>() {
    private var idColoring : Int = 0
    override fun setViewBinding(): ActivityColoringDetailBinding {
        return ActivityColoringDetailBinding.inflate(layoutInflater)
    }

    override fun initView() {
        idColoring = intent.getIntExtra("id", 0)
        binding.imgColoring.setImageResource(DataApp.getListColoring()[idColoring].img)
        binding.imgBack.tap { finish() }
    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }

}