package  com.example.doodleart.ui.splash

import android.annotation.SuppressLint
import android.view.LayoutInflater
import com.example.doodleart.ui.main.MainActivity
import com.example.doodleart.base.BaseActivity
import com.example.doodleart.databinding.ActivitySplashBinding
import com.example.doodleart.widget.setGradientText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val croutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    override fun setViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        croutineScope.launch {
            delay(5000)
            startIntro()
        }
        binding.tvAppName.setGradientText(this)
    }

    override fun viewListener() {

    }

    override fun dataObservable() {

    }

    private fun startIntro() {
        showActivity(MainActivity::class.java)
        finish()
    }

    override fun onResume() {
        super.onResume()

    }

}