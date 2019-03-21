package com.br.mytasksapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.br.mytasksapp.R;
import com.br.mytasksapp.util.Util;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Util.getApiToken() != null){
                    Util.putPref("first_access", "no");
                    startActivity(HomeActivity.class);
                }else{
                    Util.putPref("first_access", "yes");
                    startActivity(LoginActivity.class);
                }
            }
        }, 2500);
    }

    private void startActivity(Class redirect){
        startActivity(new Intent(SplashActivity.this, redirect));
        finish();
    }
}