package com.doodleart.paintcolor.drawart.ui.my_file.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.doodleart.paintcolor.drawart.R
import com.doodleart.paintcolor.drawart.base.BaseFragment
import com.doodleart.paintcolor.drawart.databinding.FragmentColorationBinding
import com.doodleart.paintcolor.drawart.roomdb.DBHelper
import com.doodleart.paintcolor.drawart.ui.my_file.MyFileDetailActivity
import com.doodleart.paintcolor.drawart.widget.gone
import com.doodleart.paintcolor.drawart.widget.visible
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
        setData()
    }

    override fun viewListener() {
    }
    private fun setData() {
        lifecycleScope.launch {
            val db = DBHelper.getDatabase(requireContext())
            val fileList = db.fileDao().getAllFiles().filter { !it.type }

            if (fileList.isEmpty() || fileList.size == 0) {
                binding.llEmpty.visible()
                binding.rcvMyFile.gone()
            }else{
                myFileAdapter = MyFileAdapter(fileList) { file ->
                    val intent = Intent(requireContext(), MyFileDetailActivity::class.java)
                    intent.putExtra("fileId", file.id)
                    intent.putExtra("checkVisible", file.type)
                    startActivity(intent)            }
                binding.rcvMyFile.adapter = myFileAdapter
                binding.llEmpty.gone()
                binding.rcvMyFile.visible()
            }
        }
    }

    override fun dataObservable() {
    }

    override fun onResume() {
        super.onResume()
        setData()
    }

}