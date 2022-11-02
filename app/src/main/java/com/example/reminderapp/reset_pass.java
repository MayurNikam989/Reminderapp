package com.example.reminderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class reset_pass extends AppCompatActivity {
    private EditText newpass,connewpass;
    private Button resetpass;
    private String mobile;
    private ProgressDialog dialogbox;
    DatabaseReference dbr = FirebaseDatabase.getInstance().getReferenceFromUrl("https://loginra-eafaf-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        newpass = findViewById(R.id.new_pass);
        connewpass = findViewById(R.id.con_new_pass);
        resetpass =findViewById(R.id.reset_pass);
        Intent intent = getIntent();
        mobile = intent.getStringExtra("Mobile");
        dialogbox = new ProgressDialog(this);

        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String new_pass = newpass.getText().toString();
                String confirm_pass = connewpass.getText().toString();

                if(new_pass.length()==0){
                    newpass.setError("Enter Correct Password");
                    newpass.requestFocus();
                }else if(confirm_pass.length()==0 || !confirm_pass.equals(new_pass)){
                    connewpass.setError("Password Not Matching");
                    connewpass.requestFocus();
                }else{
                    dbr.child("users").child(mobile).child("Password").setValue(confirm_pass);

                    dialogbox.setMessage("Please wait...");
                    dialogbox.setTitle("Reset Password");
                    dialogbox.setCanceledOnTouchOutside(false);
                    dialogbox.show();
                    Intent intent1 = new Intent(reset_pass.this, MainActivity.class);
                    startActivity(intent1);
                    Toast.makeText(reset_pass.this, "Password Changed Succesfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }
}