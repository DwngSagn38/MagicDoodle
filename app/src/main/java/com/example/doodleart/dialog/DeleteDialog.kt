package com.example.doodleart.dialog

import android.app.Activity
import android.view.LayoutInflater
import com.example.doodleart.base.BaseDialog
import com.example.doodleart.databinding.DialogDeleteBinding
import com.example.doodleart.widget.tap

class DeleteDialog (
    activity1: Activity,
    val mess : String? = null,
    val content: String? = null,
    private var action: () -> Unit,
    private var no: () -> Unit,
) : BaseDialog<DialogDeleteBinding>(activity1, true) {


    override fun getContentView(): DialogDeleteBinding {
        return DialogDeleteBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {
    }

    override fun bindView() {
        binding.root.tap { dismiss() }
        binding.apply {

            tvMess.text = mess

            tvNo.tap {
                no.invoke()
                dismiss()
            }

            tvYes.tap {
                action.invoke()
                dismiss()
            }
        }
    }
}