package com.example.doodleart.ui.coloring

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.doodleart.R
import com.example.doodleart.base.BaseActivity
import com.example.doodleart.data.DataApp
import com.example.doodleart.databinding.ActivityColoringBinding
import com.example.doodleart.widget.tap

class ColoringActivity : BaseActivity<ActivityColoringBinding>() {
    private lateinit var adapter: ColoringAdapter

    override fun setViewBinding(): ActivityColoringBinding {
        return ActivityColoringBinding.inflate(layoutInflater)
    }

    override fun initView() {
        setDataColoring()
        binding.imgBack.tap { finish() }
    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }

    private fun setDataColoring(){
        adapter = ColoringAdapter(){
            val intent = Intent(this, ColoringDetailActivity::class.java)
            intent.putExtra("id", it.id)
            startActivity(intent)
        }
        binding.rcvColoring.layoutManager = GridLayoutManager(this, 2)
        binding.rcvColoring.adapter = adapter

        adapter.addList(DataApp.getListColoring().toMutableList())
    }

}