package com.example.doodleart.ui.inpiration

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
import com.example.doodleart.databinding.ActivityInpirationBinding
import com.example.doodleart.ui.coloring.ColoringAdapter
import com.example.doodleart.ui.coloring.ColoringDetailActivity
import com.example.doodleart.widget.tap

class InpirationActivity : BaseActivity<ActivityInpirationBinding>() {
    private lateinit var adapter: InspirationAdapter

    override fun setViewBinding(): ActivityInpirationBinding {
        return ActivityInpirationBinding.inflate(layoutInflater)
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
        adapter = InspirationAdapter(){
            val intent = Intent(this, InspirationDetailActivity::class.java)
            intent.putExtra("id", it.id)
            startActivity(intent)
        }
        binding.rcvInspiration.layoutManager = GridLayoutManager(this, 2)
        binding.rcvInspiration.adapter = adapter
        adapter.addList(DataApp.getListInpiration().toMutableList())
    }

}