package com.example.reminderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class verify_otp extends AppCompatActivity {

    private Button verotp;
    private EditText getotp;
    private String mVerificationId;
    private String mobile;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        mAuth = FirebaseAuth.getInstance();
        verotp = findViewById(R.id.ver_otp);
        getotp = findViewById(R.id.getotp);

        Intent intent = getIntent();
        mobile = intent.getStringExtra("Mobile");
        sendverificationcode(mobile);

        verotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = getotp.getText().toString();
                if (code.isEmpty() || code.length() < 6) {
                    getotp.setError("Enter valid code");
                    getotp.requestFocus();
                    return;
                }
                verificationCode(code);
            }


        });
    }

    private void sendverificationcode(String mobile) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+mobile)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code by sms
            String code = phoneAuthCredential.getSmsCode();
            if (code != null)
            {
                getotp.setText(code);
                verificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(verify_otp.this, "Verification failed.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
        }
    };


    private void verificationCode(String code)
    {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code);
        SignInWithPhoneAuthCredential(credential);
    }

    private  void SignInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(verify_otp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(verify_otp.this,reset_pass.class);
                            intent.putExtra("Mobile", mobile );
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            String message = "Somthing is wrong,you entered wrong otp.";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(verify_otp.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}