package com.example.equiclubapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FaceActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN = 5000;
    Animation top, bottom;
    ImageView image;
    TextView main, slogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_face);
        //Animations
        top = AnimationUtils.loadAnimation(this,R.anim.top);
        bottom = AnimationUtils.loadAnimation(this,R.anim.bottom);
        //Hooks
        image = findViewById(R.id.imageView);
        main = findViewById(R.id.textView);
        slogan = findViewById(R.id.textView2);
        //Affectation of animations
        image.setAnimation(top);
        main.setAnimation(bottom);
        slogan.setAnimation(bottom);
        //Going to login Page
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(com.example.equiclubapp.FaceActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
}