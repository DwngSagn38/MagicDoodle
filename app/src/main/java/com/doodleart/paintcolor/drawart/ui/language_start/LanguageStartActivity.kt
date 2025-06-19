package  com.doodleart.paintcolor.drawart.ui.language_start

import android.content.Intent
import android.os.Bundle
import com.doodleart.paintcolor.drawart.utils.SystemUtil
import com.doodleart.paintcolor.drawart.base.BaseActivity
import com.doodleart.paintcolor.drawart.databinding.ActivityLangugeStartBinding
import com.doodleart.paintcolor.drawart.model.LanguageModel
import com.doodleart.paintcolor.drawart.ui.intro.IntroActivity
import com.doodleart.paintcolor.drawart.widget.tap
import com.doodleart.paintcolor.drawart.widget.visible
import java.util.Locale

class LanguageStartActivity : BaseActivity<ActivityLangugeStartBinding>() {
    private lateinit var adapter: LanguageStartAdapter
    private var listLanguage: ArrayList<LanguageModel> = ArrayList()
    private var codeLang = ""
    private var isDialogVisible = false

    override fun setViewBinding(): ActivityLangugeStartBinding {
        return ActivityLangugeStartBinding.inflate(layoutInflater)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun initView() {
        adapter = LanguageStartAdapter(this, onClick = {
            codeLang = it.code
            adapter.setCheck(it.code)
            binding.ivDone.visible()
            adapter.notifyDataSetChanged()
        })

        binding.recyclerView.adapter = adapter
    }

    override fun viewListener() {
        binding.ivDone.tap {
            SystemUtil.saveLocal(baseContext, codeLang)
            var bundle = Bundle()
            bundle.putString("language_name", codeLang)
            startActivity(Intent(this@LanguageStartActivity, IntroActivity::class.java))
            finish()
        }
    }

    override fun dataObservable() {
        setCodeLanguage()
        initData()
    }

    private fun setCodeLanguage() {
        val codelangDefault = Locale.getDefault().language
        val langDefault = arrayOf("fr", "pt", "es", "de", "in", "en", "hi", "vi", "ja")

        codeLang = if (SystemUtil.getPreLanguage(this).equals("")) {
            if (!mutableListOf(*langDefault).contains(codelangDefault)) {
                "en"
            } else {
                codelangDefault
            }
        } else {
            SystemUtil.getPreLanguage(this)
        }
    }

    private fun initData() {
        var pos = 0
        listLanguage.clear()
        listLanguage.addAll(SystemUtil.listLanguage())
//        listLanguage.forEachIndexed { index, languageModel ->
//            if (languageModel.code == codeLang) {
//                pos = index
//                binding.ivDone.visible()
//                return@forEachIndexed
//            }
//        }
//        val temp = listLanguage[pos]
//        temp.active = true
//        listLanguage.removeAt(pos)
//        listLanguage.add(0, temp)

        adapter.addList(listLanguage)
    }



}