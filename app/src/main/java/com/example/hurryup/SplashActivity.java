package com.example.hurryup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceStare) {
        super.onCreate(savedInstanceStare);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), SpActivity.class);
                startActivity(intent);
                finish();
            }
        },1000); // 1초 있다 스타트 액티비티로
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}