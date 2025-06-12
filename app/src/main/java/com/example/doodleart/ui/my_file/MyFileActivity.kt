package com.example.doodleart.ui.my_file

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.doodleart.R
import com.example.doodleart.base.BaseActivity
import com.example.doodleart.databinding.ActivityMyFileBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MyFileActivity : BaseActivity<ActivityMyFileBinding>() {
    override fun setViewBinding(): ActivityMyFileBinding {
        return ActivityMyFileBinding.inflate(layoutInflater)
    }

    override fun initView() {

        val tabIcons = listOf(R.drawable.ic_coloring, R.drawable.ic_drawing)
        val tabTitles = listOf("Coloration", "Draw")

        binding.viewPager2.adapter = MyFilePagerAdapter(this)

        TabLayoutMediator(binding.tabLayout,  binding.viewPager2) { tab, position ->
            val tabView = LayoutInflater.from(this).inflate(R.layout.tab_item, null)
            val tabIcon = tabView.findViewById<ImageView>(R.id.tabIcon)
            val tabText = tabView.findViewById<TextView>(R.id.tabText)

            tabIcon.setImageResource(tabIcons[position])
            tabText.text = tabTitles[position]

            tab.customView = tabView
        }.attach()

        // Force select the first tab and style it manually
        binding.tabLayout.getTabAt(0)?.let { firstTab ->
            binding.tabLayout.selectTab(firstTab) // Select tab 0
            // Manually call your selection logic
            firstTab.customView?.apply {
                setBackgroundResource(R.drawable.bg_tab_selected)
                findViewById<ImageView>(R.id.tabIcon).setColorFilter(ContextCompat.getColor(context, R.color.pink))
                findViewById<TextView>(R.id.tabText).setTextColor(ContextCompat.getColor(context, R.color.pink))
            }
        }

    }

    override fun viewListener() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.customView?.apply {
                    setBackgroundResource(R.drawable.bg_tab_selected)
                    findViewById<ImageView>(R.id.tabIcon).setColorFilter(ContextCompat.getColor(context, R.color.pink))
                    findViewById<TextView>(R.id.tabText).setTextColor(ContextCompat.getColor(context, R.color.pink))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.apply {
                    setBackgroundResource(R.drawable.bg_tab_unselected)
                    findViewById<ImageView>(R.id.tabIcon).setColorFilter(ContextCompat.getColor(context, R.color.tab_unselected))
                    findViewById<TextView>(R.id.tabText).setTextColor(ContextCompat.getColor(context, R.color.tab_unselected))
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        binding.imgBack.setOnClickListener { finish() }

    }

    override fun dataObservable() {
    }

}