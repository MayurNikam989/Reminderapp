package com.example.reminderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class home extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TextView logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            SharedPreferences sp = getSharedPreferences(MainActivity.PREFS_NAME,0);
            SharedPreferences.Editor editor = sp.edit();
            @Override
            public void onClick(View view) {
                editor.putBoolean("HasLoggedIn", false);
                editor.commit();
                Toast.makeText(home.this, "Logged Out Succesfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

    }
}