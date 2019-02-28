package com.br.mytasksapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class MyTaskApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        /* Set instance */
        MyTaskApplication.context = getApplicationContext();
    }

    public static Context context(){
        return MyTaskApplication.context;
    }
}