package com.example.reminderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class home extends AppCompatActivity {
    private static final String NAME = "name";
    public static String PREFS_NAME = "Myprefsfile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TextView logout = findViewById(R.id.logout);
        TextView setname = findViewById(R.id.setname);
        ProgressDialog dialogbox = new ProgressDialog(this);
        SharedPreferences sp1 = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        String name = sp1.getString(NAME,"");
        setname.setText(name);

        logout.setOnClickListener(new View.OnClickListener() {
            SharedPreferences sp = getSharedPreferences(MainActivity.PREFS_NAME,0);
            SharedPreferences.Editor editor = sp.edit();
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(home.this);
                builder.setMessage("Are you sure you want to Log out?")
                        .setCancelable(false)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editor.putBoolean("HasLoggedIn", false);
                                editor.commit();
                                dialogbox.setMessage("Please wait...");
                                dialogbox.setTitle("Log Out");
                                dialogbox.setCanceledOnTouchOutside(false);
                                dialogbox.show();
                                Toast.makeText(home.this, "LogOut Succesfull", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
        });






    }


}