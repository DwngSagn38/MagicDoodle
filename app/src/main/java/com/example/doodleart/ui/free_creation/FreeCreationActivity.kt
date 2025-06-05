package com.example.doodleart.ui.free_creation

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.doodleart.R
import com.example.doodleart.base.BaseActivity
import com.example.doodleart.databinding.ActivityFreeCreationBinding
import com.example.doodleart.widget.tap

class FreeCreationActivity : BaseActivity<ActivityFreeCreationBinding>() {
    override fun setViewBinding(): ActivityFreeCreationBinding {
        return ActivityFreeCreationBinding.inflate(layoutInflater)
    }

    override fun initView() {
    }

    override fun viewListener() {
        binding.ic4.tap {
            val items =
                arrayOf("1 phần (nét đơn)", "2 phần", "3 phần", "4 phần", "5 phần", "6 phần", "7phan","8phan","phan9")
            AlertDialog.Builder(this)
                .setTitle("Chọn số phần đối xứng")
                .setItems(items) { _, which ->
                    binding.mandalaView.setSymmetryCount(which + 1)
                    Toast.makeText(this, "Đã chọn ${which + 1} phần", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
        binding.icSelecedBackground.setOnClickListener {
            val colors = arrayOf("Trắng", "Vàng", "Xanh", "Hồng", "Đen")
            val colorValues = arrayOf(Color.WHITE, Color.YELLOW, Color.BLUE, Color.MAGENTA, Color.BLACK)
            AlertDialog.Builder(this)
                .setTitle("Chọn màu nền")
                .setItems(colors) { _, which ->
                    binding.mandalaView.setBackgroundColorCustom(colorValues[which])
                }
                .show()
        }
        binding.icEraser.setOnClickListener {
            val currentlyOn = binding.mandalaView.isEraserOn()
            binding.mandalaView.setEraser(!currentlyOn)
//            buttonEraser.text = if (!currentlyOn) "Tắt tẩy" else "Bật tẩy"
        }
        binding.icUndu.setOnClickListener {
            binding.mandalaView.undo()
        }

        binding.icRedu.setOnClickListener {
            binding.mandalaView.redo()
        }
    }
    override fun dataObservable() {
    }

}