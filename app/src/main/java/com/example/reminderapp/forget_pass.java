package com.example.reminderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class forget_pass extends AppCompatActivity {
    private Button getotp,checkotp;
    private EditText phone,otp;
    private TextView reotp;
    String mobilepattern = "[0-9]{10}";
    DatabaseReference dbr = FirebaseDatabase.getInstance().getReferenceFromUrl("https://loginra-eafaf-default-rtdb.firebaseio.com/");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
         getotp = findViewById(R.id.getotp);
         checkotp = findViewById(R.id.verifyotp);
         phone = findViewById(R.id.ipmobile);
         otp = findViewById(R.id.otp);
         reotp = findViewById(R.id.resendotp);


         getotp.setOnClickListener(new View.OnClickListener() {
             boolean visible1;
             @Override
             public void onClick(View view) {
                 String mobile = phone.getText().toString();
                 if(!mobile.matches(mobilepattern) || mobile.length()==0){
                     phone.setError("Enter correct Mobile");
                     phone.requestFocus();
                 }else{
                     dbr.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                             if(snapshot.hasChild(mobile)){
                                 visible1 = !visible1;
                                 checkotp.setVisibility(visible1 ? View.VISIBLE : View.VISIBLE);
                                 otp.setVisibility(visible1 ? View.VISIBLE : View.VISIBLE);
                                 reotp.setVisibility(visible1 ? View.VISIBLE : View.VISIBLE);
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