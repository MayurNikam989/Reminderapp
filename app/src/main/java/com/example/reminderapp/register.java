package com.example.reminderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class register extends AppCompatActivity {
    private EditText name,remail,phone,password;
    String emailpattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";
    String mobilepattern = "[0-9]{10}";
    ProgressDialog dialogbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button regbtn = findViewById(R.id.reg);
        name = findViewById(R.id.name);
        remail = findViewById(R.id.regemail);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.pass);
        dialogbox = new ProgressDialog(this);
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReferenceFromUrl("https://loginra-eafaf-default-rtdb.firebaseio.com/");

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nam = name.getText().toString();
                String mel = remail.getText().toString();
                String mobile = phone.getText().toString();
                String pw = password.getText().toString();
                if(nam.length()==0) {
                    name.setError("Enter Name");
                    name.requestFocus();
                }else if(!mel.matches(emailpattern) || mel.length()==0){
                    remail.setError("Enter Correct Email");
                    remail.requestFocus();
                }else if(!mobile.matches(mobilepattern) || mobile.length()==0){
                    phone.setError("Enter Correct Mobile");
                    phone.requestFocus();
                }else if(pw.length()==0){
                    password.setError("Enter Password");
                    password.requestFocus();
                }else{

                    dbr.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(mobile)) {
                                Toast.makeText(register.this, "Mobile no is already registered", Toast.LENGTH_SHORT).show();
                            }else{

                                dbr.child("users").child(mobile).child("Name").setValue(nam);
                                dbr.child("users").child(mobile).child("Email").setValue(mel);
                                dbr.child("users").child(mobile).child("Password").setValue(pw);
                                dialogbox.setMessage("Please wait While Registration...");
                                dialogbox.setTitle("Registration");
                                dialogbox.setCanceledOnTouchOutside(false);
                                dialogbox.show();
                                Toast.makeText(register.this, "Registered SuccesFully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }

            }
        });


    }
}