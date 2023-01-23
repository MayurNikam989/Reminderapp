package com.example.reminderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class splash_scr extends AppCompatActivity {
    private ImageView img;
    private TextView ra;
    Animation top,anim2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_scr);

        img = findViewById(R.id.img);
        ra = findViewById(R.id.rapp);
        top = AnimationUtils.loadAnimation(this,R.anim.top);
        anim2 = AnimationUtils.loadAnimation(this,R.anim.anim2);
        img.setAnimation(top);
        ra.setAnimation(anim2);
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1500);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    SharedPreferences sp = getSharedPreferences(MainActivity.PREFS_NAME,0);
                    boolean HasLoggedIn = sp.getBoolean("HasLoggedIn", false);

                    if(false){
                        Intent intent1 =  new Intent(splash_scr.this,home.class);
                        startActivity(intent1);
                        finishAffinity();
                    }else{
                        Intent intent =  new Intent(splash_scr.this,MainActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                }
            }
        };
        thread.start();

    }
}
