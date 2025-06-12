package com.example.doodleart.ui.my_file

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.doodleart.ui.my_file.fragment.ColorationFragment
import com.example.doodleart.ui.my_file.fragment.DrawFragment

class MyFilePagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ColorationFragment()
            1 -> DrawFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}