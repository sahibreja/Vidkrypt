package com.example.vidkrypt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.vidkrypt.login.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {
    private ImageView logo;
    private TextView  logoName;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen2);
        init();
        FirebaseUser currentUser = auth.getCurrentUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //YoYo.with(Techniques.RollIn).duration(100).repeat(0).playOn(logo);
                //YoYo.with(Techniques.FadeOut).duration(100).repeat(0).playOn(logoName);
                if(currentUser != null){
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                }else
                {
                    startActivity(new Intent(SplashScreenActivity.this,IntroScreenActivity.class));
                    finish();
                }
            }
        },2500);

    }
    private void init()
    {
        //logo=findViewById(R.id.logo);
//        logoName=findViewById(R.id.logoText);
        auth = FirebaseAuth.getInstance();
    }
}