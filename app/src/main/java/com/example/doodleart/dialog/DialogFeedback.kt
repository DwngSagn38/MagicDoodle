package com.example.doodleart.view.dialog

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Toast
import com.example.doodleart.R
import com.example.doodleart.databinding.DialogFeedbackBinding
import com.example.doodleart.sharePreferent.SharePrefUtils
import com.example.doodleart.utils.helper.Default
import com.example.doodleart.base.BaseDialog
import com.example.doodleart.widget.tap


class DialogFeedback(activity: Activity) :
    BaseDialog<DialogFeedbackBinding>(activity, true) {

    override fun getContentView(): DialogFeedbackBinding {
        return DialogFeedbackBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {
    }

    override fun bindView() {
        binding.apply {
            tvSend.tap {
                if (binding.edtFeedback.text.toString().isEmpty()) {
                    Toast.makeText(activity, R.string.please_send_us, Toast.LENGTH_SHORT).show()
                    return@tap
                }
                dismiss()
                sendMail()
            }
            tvCancel.tap {
                dismiss()
            }
        }
    }

    private fun sendMail() {
        val uriText =
            "mailto:${Default.EMAIL}?subject=${Default.SUBJECT}&" +
                    "body=Content : \n${binding.edtFeedback.text.toString().trim()}".trimIndent()
        val uri = Uri.parse(uriText)
        val sendIntent = Intent(Intent.ACTION_SENDTO)
        sendIntent.data = uri
        try {
            activity.startActivity(Intent.createChooser(sendIntent, "Send Email"))
            SharePrefUtils.forceRated(
                activity
            )
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                activity,
                R.string.There_is_no_email,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}