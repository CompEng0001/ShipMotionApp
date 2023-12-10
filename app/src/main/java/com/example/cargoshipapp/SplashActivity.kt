package com.example.cargoshipapp;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {
    TextView XTV;
    TextView YTV;
    TextView ZTV;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_splash);
        this.ZTV = (TextView) findViewById(R.id.ZAxisTVID);
        this.YTV = (TextView) findViewById(R.id.YAxisTVID);
        this.XTV = (TextView) findViewById(R.id.XAxisTVID);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{-180, 180});
        ofInt.setDuration(3000);
        ofInt.setRepeatCount(-1);
        ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                TextView textView = SplashActivity.this.ZTV;
                textView.setText(valueAnimator.getAnimatedValue().toString() + "°");
                TextView textView2 = SplashActivity.this.YTV;
                textView2.setText(valueAnimator.getAnimatedValue().toString() + "°");
                TextView textView3 = SplashActivity.this.XTV;
                textView3.setText(valueAnimator.getAnimatedValue().toString() + "°");
            }
        });
        ofInt.start();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, 3000);
    }
}
