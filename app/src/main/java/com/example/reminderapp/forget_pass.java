package com.example.reminderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;


public class forget_pass extends AppCompatActivity {
    private Button getotp,checkotp;
    private EditText phone,otp;
    private TextView reotp;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;
    private String mVerificationID;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    String mobilepattern = "[0-9]{10}";
    DatabaseReference dbr = FirebaseDatabase.getInstance().getReferenceFromUrl("https://loginra-eafaf-default-rtdb.firebaseio.com/");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
         getotp = findViewById(R.id.getotp);
         phone = findViewById(R.id.ipmobile);
         mAuth = FirebaseAuth.getInstance();



         getotp.setOnClickListener(new View.OnClickListener() {
             boolean visible1;
             @Override
             public void onClick(View view) {
                 final String mobile = phone.getText().toString();
                 if(!mobile.matches(mobilepattern) || mobile.length()==0){
                     phone.setError("Enter correct Mobile");
                     phone.requestFocus();
                 }else{
                     dbr.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                             if(snapshot.hasChild(mobile)){
                                 Intent intent = new Intent(forget_pass.this,verify_otp.class);
                                 intent.putExtra("Mobile",mobile);
                                 startActivity(intent);
                                 finishAffinity();
                             }else{
                                 Toast.makeText(forget_pass.this, "Mobile No not Registered", Toast.LENGTH_SHORT).show();
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