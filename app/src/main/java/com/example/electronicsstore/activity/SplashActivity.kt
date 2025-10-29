package com.example.electronicsstore.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.electronicsstore.R
import com.example.electronicsstore.prefs.DataStoreManager
import com.example.electronicsstore.utils.GlobalFunction.startActivity
import com.example.electronicsstore.utils.StringUtil.isEmpty

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ this.goToActivity() }, 2000)
    }

    private fun goToActivity() {
        if (DataStoreManager.user != null
            && !isEmpty(DataStoreManager.user?.email)
        ) {
            startActivity(this, MainActivity::class.java)
        } else {
            startActivity(this, LoginActivity::class.java)
        }
        finish()
    }
}
