package com.doodleart.paintcolor.drawart.dialog

import android.app.Activity
import android.view.LayoutInflater
import com.doodleart.paintcolor.drawart.base.BaseDialog
import com.doodleart.paintcolor.drawart.databinding.DialogDeleteBinding
import com.doodleart.paintcolor.drawart.databinding.PopupNewFileBinding
import com.doodleart.paintcolor.drawart.widget.tap

class DeleteDialog (
    activity1: Activity,
    val title : String? = null,
    val mess : String? = null,
    val content: String? = null,
    private var action: () -> Unit,
    private var no: () -> Unit,
) : BaseDialog<PopupNewFileBinding>(activity1, true) {


    override fun getContentView(): PopupNewFileBinding {
        return PopupNewFileBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {
    }

    override fun bindView() {
        binding.root.tap { dismiss() }
        binding.apply {

            if(title != null) tv1.text = title
            if (mess != null) tv2.text = mess
            if (content != null) tvSave.text = content
            tvDiscard.tap {
                no.invoke()
                dismiss()
            }

            tvSave.tap {
                action.invoke()
                dismiss()
            }

        }
    }
}