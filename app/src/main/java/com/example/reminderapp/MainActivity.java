package com.example.reminderapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private EditText phone,password;
    DatabaseReference dbr = FirebaseDatabase.getInstance().getReferenceFromUrl("https://loginra-eafaf-default-rtdb.firebaseio.com/");

    String mobilepattern = "[0-9]{10}";
    public static String PREFS_NAME = "Myprefsfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Finding view by id
        TextView register = findViewById(R.id.register);
        TextView forpass = findViewById(R.id.fpass);
        Button login = findViewById(R.id.login);
        phone = findViewById(R.id.phone2);
        password = findViewById(R.id.password);

        //To skip login activity after logged in
        SharedPreferences sp = getSharedPreferences(MainActivity.PREFS_NAME,0);
        SharedPreferences.Editor editor = sp.edit();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To get input from User
                String mo = phone.getText().toString();
                String pass = password.getText().toString();

                //Checking all field
                if(mo.length()==0 || !mo.matches(mobilepattern)){
                    phone.requestFocus();
                    phone.setError("Enter Correct Mobile");
                }else if(pass.length()==0){
                    password.requestFocus();
                    password.setError("Enter Password");
                }else{

                    //connecting database
                    dbr.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //checking  phone no is registered or not
                            if(snapshot.hasChild(mo)){
                                final String getpass = snapshot.child(mo).child("Password").getValue(String.class);
                                if (getpass.equals(pass)) {

                                    editor.putBoolean("HasLoggedIn", true);
                                    editor.commit();

                                    Toast.makeText(MainActivity.this, "Login Succesfully", Toast.LENGTH_SHORT).show();
                                    Intent intent1 = new Intent(getApplicationContext(),home.class);
                                    startActivity(intent1);
                                    finishAffinity();
                                }else{
                                    Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(MainActivity.this, "Mobile no. Not Registerd", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), register.class);
                startActivity(intent);
            }
        });
    }


}