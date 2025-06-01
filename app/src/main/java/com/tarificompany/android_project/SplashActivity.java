package com.tarificompany.android_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private ImageView logoImageView;
    private TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setUpViews();
    }

    public void setUpViews() {
        logoImageView = findViewById(R.id.logoImageView);
        txtSlogan = findViewById(R.id.txtSlogan);

        Animation logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_anim);
        Animation textAnim = AnimationUtils.loadAnimation(this, R.anim.text_fade);

        logoImageView.startAnimation(logoAnim);
        txtSlogan.startAnimation(textAnim);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                Intent intent = new Intent(SplashActivity.this,TeacherActivity.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.e("SplashError", "Failed to start TeacherDashboardActivity: " + e.getMessage(), e);
                try {
                    Intent fallbackIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(fallbackIntent);
                    finish();
                } catch (Exception fallbackException) {
                    Log.e("SplashError", "Failed to start LoginActivity: " + fallbackException.getMessage(), fallbackException);
                    Toast.makeText(SplashActivity.this, "Error starting app. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        }, 2000);
    }
}