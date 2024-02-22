package com.onedeveloper.think.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.onedeveloper.think.R
import com.onedeveloper.think.databinding.SplashscreenBinding

class Splashscreen : AppCompatActivity() {
    var binding :SplashscreenBinding? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = SplashscreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.hide()
        Handler().postDelayed({
            val homeIntent = Intent(this@Splashscreen, MainActivity::class.java)
            startActivity(homeIntent)
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }

    companion object {
        var SPLASH_TIME_OUT = 1300
    }
}