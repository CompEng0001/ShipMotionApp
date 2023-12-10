package com.example.cargoshipappkotlin

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ValueAnimator;
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView

class SplashActivity : AppCompatActivity() {
    private lateinit var XTV: TextView
    private lateinit var YTV: TextView
    private lateinit var ZTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        ZTV = findViewById(R.id.ZAxisTVID)
        YTV = findViewById(R.id.YAxisTVID)
        XTV = findViewById(R.id.XAxisTVID)
    }

    override fun onStart() {
        super.onStart()

        val ofInt = ValueAnimator.ofInt(-180, 180)
        ofInt.duration = 3000
        ofInt.repeatCount = ValueAnimator.INFINITE
        ofInt.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue.toString() + "°"
            ZTV.text = animatedValue
            YTV.text = animatedValue
            XTV.text = animatedValue
        }
        ofInt.start()

        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, 3000)
    }
}
