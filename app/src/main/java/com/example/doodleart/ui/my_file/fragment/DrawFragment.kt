package com.example.doodleart.ui.my_file.fragment

import android.content.Intent
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
import com.example.doodleart.databinding.FragmentDrawBinding
import com.example.doodleart.roomdb.DBHelper
import com.example.doodleart.ui.my_file.MyFileDetailActivity
import com.example.doodleart.view.base.BaseFragment
import com.example.doodleart.widget.gone
import com.example.doodleart.widget.visible
import kotlinx.coroutines.launch


class DrawFragment : BaseFragment<FragmentColorationBinding>() {
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
        // load data trong coroutine

    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }

    private fun setData(){
        lifecycleScope.launch {
            val db = DBHelper.getDatabase(requireContext())
            val fileList = db.fileDao().getAllFiles().filter { !it.type }
            if (fileList.isEmpty() || fileList.size == 0) {
                binding.llEmpty.visible()
                binding.rcvMyFile.gone()
            }else{
                binding.llEmpty.gone()
                binding.rcvMyFile.visible()
                myFileAdapter = MyFileAdapter(fileList) { file ->
                    val intent = Intent(requireContext(), MyFileDetailActivity::class.java)
                    intent.putExtra("fileId", file.id)
                    intent.putExtra("checkVisible", file.type)
                    startActivity(intent)
                }
            }

            binding.rcvMyFile.adapter = myFileAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        setData()
    }

}