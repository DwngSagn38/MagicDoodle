package com.example.doodleart.ui.main

import android.content.Intent
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.doodleart.R
import com.example.doodleart.base.BaseActivity
import com.example.doodleart.databinding.ActivityMainBinding
import com.example.doodleart.ui.free_creation.FreeCreationActivity
import com.example.doodleart.widget.tap
import com.example.doodleart.ui.coloring.ColoringActivity
import com.example.doodleart.ui.inpiration.InpirationActivity
import com.example.doodleart.ui.my_file.MyFileActivity
import com.example.doodleart.ui.setting.SettingActivity
import com.example.doodleart.utils.showColorPicker
import com.example.doodleart.widget.tap

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private var currentColorInt: Int = Color.WHITE
    private var selectedType: String? = "null"


    override fun setViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {

        binding.btnSetting.tap { showActivity(SettingActivity::class.java) }

    }

    override fun viewListener() {
        binding.tvFreeCreation.tap {
            showGhostSelectionPopup()
        }
        binding.tvColoring.tap { showActivity(ColoringActivity::class.java) }
        binding.tvMyFile.tap {
            showActivity(MyFileActivity::class.java)
        }
        binding.tvInspiration.tap {
            showActivity(InpirationActivity::class.java)
        }
    }

    private fun showGhostSelectionPopup() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_start_drawing, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        dialog.setCanceledOnTouchOutside(true)
        val tv_9_11 = dialogView.findViewById<TextView>(R.id.tv_9_11)
        val tv_1_1 = dialogView.findViewById<TextView>(R.id.tv_1_1)
        val tv_4_5 = dialogView.findViewById<TextView>(R.id.tv_4_5)
        val btnNext = dialogView.findViewById<TextView>(R.id.tv_next)
        val btnCancel = dialogView.findViewById<TextView>(R.id.tv_cancel)
        val bg = dialogView.findViewById<ConstraintLayout>(R.id.bg_popup)

        fun selectGhost(selectedView: TextView, type: String) {
            tv_9_11.setBackgroundResource(R.drawable.bg_conner_e2dcee)
            tv_1_1.setBackgroundResource(R.drawable.bg_conner_e2dcee)
            tv_4_5.setBackgroundResource(R.drawable.bg_conner_e2dcee)
            selectedView.setBackgroundResource(R.drawable.bg_conner_e2dcee_selected)
            selectedType = type
        }

        tv_9_11.setOnClickListener {
            selectGhost(tv_9_11, "9:16")
        }
        bg.setOnClickListener {
            dialog.dismiss()
        }
        tv_1_1.setOnClickListener {
            selectGhost(tv_1_1, "1:1")
        }

        tv_4_5.setOnClickListener {
            selectGhost(tv_4_5, "4:5")
        }
        btnNext.setOnClickListener {
            if (selectedType != "null") {
                val intent = Intent(this, FreeCreationActivity::class.java)
                intent.putExtra("aspect_ratio", selectedType)
                startActivity(intent)
            } else {
                Toast.makeText(this, R.string.please_select_type, Toast.LENGTH_SHORT).show()
            }
        }
        btnCancel.tap {
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        binding.tvMyFile.tap { showActivity(MyFileActivity::class.java) }
    }

    override fun dataObservable() {
    }

}