package com.example.gmart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.gmart.`object`.LoginSP

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        LoginSP.initLoginSP(this)

        Handler().postDelayed({
            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}