package com.doodleart.paintcolor.drawart.dialog

import android.app.Activity
import android.view.LayoutInflater
import com.doodleart.paintcolor.drawart.base.BaseDialog
import com.doodleart.paintcolor.drawart.databinding.DialogPermissionBinding


class PermissionDialog(
    activity1: Activity,
    private var action: () -> Unit
) : BaseDialog<DialogPermissionBinding>(activity1, true) {
    override fun getContentView(): DialogPermissionBinding {
        return DialogPermissionBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {

    }

    override fun bindView() {
        binding.apply {
//            txtGo.tap {
//                action.invoke()
//                dismiss()
//            }
        }
    }


}