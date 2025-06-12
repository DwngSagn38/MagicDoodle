package com.example.doodleart.ui.my_file.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.doodleart.R
import com.example.doodleart.databinding.FragmentColorationBinding
import com.example.doodleart.roomdb.DBHelper
import com.example.doodleart.view.base.BaseFragment
import kotlinx.coroutines.launch

class ColorationFragment : BaseFragment<FragmentColorationBinding>() {
    private lateinit var myFileAdapter: MyFileAdapter

    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentColorationBinding {
        return FragmentColorationBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        binding.rcvMyFile.layoutManager = GridLayoutManager(requireContext(), 2)

        // load data trong coroutine
        lifecycleScope.launch {
            val db = DBHelper.getDatabase(requireContext())
            val fileList = db.fileDao().getAllFiles().filter { it.type }

            myFileAdapter = MyFileAdapter(fileList) { file ->
                Toast.makeText(requireContext(), "Click: ${file.path}", Toast.LENGTH_SHORT).show()
                // Bạn có thể mở file trong ZoomablePaintView để vẽ tiếp
            }

            binding.rcvMyFile.adapter = myFileAdapter
        }
    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }

}