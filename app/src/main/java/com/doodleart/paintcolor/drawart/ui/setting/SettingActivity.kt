package com.doodleart.paintcolor.drawart.ui.setting

import android.app.AlertDialog
import android.content.Intent
import com.doodleart.paintcolor.drawart.R
import com.doodleart.paintcolor.drawart.base.BaseActivity
import com.doodleart.paintcolor.drawart.databinding.ActivitySettingBinding
import com.doodleart.paintcolor.drawart.ui.language.LanguageActivity

import com.doodleart.paintcolor.drawart.utils.helper.HelperMenu

import com.doodleart.paintcolor.drawart.widget.tap
import java.io.File

class SettingActivity : BaseActivity<ActivitySettingBinding>() {
    private var helperMenu: HelperMenu? = null
    override fun setViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }

    override fun initView() {
        helperMenu = HelperMenu(this)
    }

    override fun viewListener() {
//        binding.tvPath.text = ".../${getString(R.string.app_name)}"

        binding.apply {
//            clOutputPath.tap {
//                val path =
//                    AppConstant.getKEY_FILE_MUSIC() + "/" + getString(R.string.app_name)
//                AlertDialog.Builder(this@SettingActivity)
//                    .setTitle(getString(R.string.output_path_title))
//                    .setMessage(path)
//                    .setPositiveButton(getString(R.string.close)) { dialog, _ -> dialog.dismiss() }
//                    .show()
//
//            }
            ivBack.tap { finish() }
            clRate.tap { helperMenu?.showDialogRate(false) }
            clShare.tap { helperMenu?.showShareApp() }
            clPolicy.tap { helperMenu?.showPolicy() }
            clLanguage.tap { showActivity(LanguageActivity::class.java) }
            tvFeedback.tap { helperMenu?.showDialogFeedback() }

        }
    }

    override fun dataObservable() {
    }
}