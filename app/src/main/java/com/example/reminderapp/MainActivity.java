package com.example.reminderapp;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import android.app.ProgressDialog;
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
    private static final String NAME = "name";

    private EditText phone, password;
//    BeginSignInRequest signInRequest;
//    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
//    private boolean showOneTapUI = true;
//    FirebaseAuth mAuth;
//    SignInClient oneTapClient;




    DatabaseReference dbr = FirebaseDatabase.getInstance().getReferenceFromUrl("https://loginra-eafaf-default-rtdb.firebaseio.com/");

    String mobilepattern = "[0-9]{10}";
    public static String PREFS_NAME = "Myprefsfile";


//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            startActivity(new Intent(MainActivity.this, home.class));
//        }
//    }


    // ...

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode) {
//            case REQ_ONE_TAP:
//                try {
//
//                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
//                    String idToken = credential.getGoogleIdToken();
//                    if (idToken !=  null) {
//                        // Got an ID token from Google. Use it to authenticate
//                        // with Firebase.
////                        Log.d(TAG, "Got ID token.");
//                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
//                        mAuth.signInWithCredential(firebaseCredential)
//                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<AuthResult> task) {
//                                        if (task.isSuccessful()) {
//                                            // Sign in success, update UI with the signed-in user's information
////                                            Log.d(TAG, "signInWithCredential:success");
//                                            FirebaseUser user = mAuth.getCurrentUser();
//                                            Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            // If sign in fails, display a message to the user.
////                                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                                            Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });
//                    }
//                } catch (ApiException e) {
//                    // ...
//                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
//
//                }
//                break;
//        }
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Finding view by id
        TextView register = findViewById(R.id.register);
        TextView forpass = findViewById(R.id.fpass);
        phone = findViewById(R.id.phone2);
        password = findViewById(R.id.password);
        //To skip login activity after logged in
        SharedPreferences sp = getSharedPreferences(MainActivity.PREFS_NAME,0);
        SharedPreferences.Editor editor = sp.edit();
        ProgressDialog dialogbox = new ProgressDialog(this);

        Button login = findViewById(R.id.login);
        // ...
        // Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();



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
                                    final String setname = snapshot.child(mo).child("Name").getValue(String.class);
                                    editor.putBoolean("HasLoggedIn", true);
                                    editor.commit();
                                    dialogbox.setMessage("Please wait...");
                                    dialogbox.setTitle("Login");
                                    dialogbox.setCanceledOnTouchOutside(false);
                                    dialogbox.show();
                                    SharedPreferences sp1 = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
                                    SharedPreferences.Editor editor1 = sp1.edit();
                                    editor1.putString(NAME,snapshot.child(mo).child("Name").getValue((String.class)));
                                    editor1.apply();
                                    Toast.makeText(MainActivity.this, "Login Succesfull", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(MainActivity.this, register.class);
                startActivity(intent);
            }
        });

        forpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(MainActivity.this, forget_pass.class);
                startActivity(intent2);
            }
        });

    }





}













































//login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //To get input from User
//                String mo = phone.getText().toString();
//                String pass = password.getText().toString();
//
//                //Checking all field
//                if(mo.length()==0 || !mo.matches(mobilepattern)){
//                    phone.requestFocus();
//                    phone.setError("Enter Correct Mobile");
//                }else if(pass.length()==0){
//                    password.requestFocus();
//                    password.setError("Enter Password");
//                }else{
//
//
//                    //connecting database
//                    dbr.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            //checking  phone no is registered or not
//                            if(snapshot.hasChild(mo)){
//                                final String getpass = snapshot.child(mo).child("Password").getValue(String.class);
//                                if (getpass.equals(pass)) {
//                                    final String setname = snapshot.child(mo).child("Name").getValue(String.class);
//                                    editor.putBoolean("HasLoggedIn", true);
//                                    editor.commit();
//                                    dialogbox.setMessage("Please wait...");
//                                    dialogbox.setTitle("Login");
//                                    dialogbox.setCanceledOnTouchOutside(false);
//                                    dialogbox.show();
//                                    SharedPreferences sp1 = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
//                                    SharedPreferences.Editor editor1 = sp1.edit();
//                                    editor1.putString(NAME,snapshot.child(mo).child("Name").getValue((String.class)));
//                                    editor1.apply();
//                                    Toast.makeText(MainActivity.this, "Login Succesfull", Toast.LENGTH_SHORT).show();
//                                    Intent intent1 = new Intent(getApplicationContext(),home.class);
//
//                                    startActivity(intent1);
//                                    finishAffinity();
//                                }else{
//                                    Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
//                                }
//                            }else{
//                                Toast.makeText(MainActivity.this, "Mobile no. Not Registerd", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//                }
//            }
//        });








//register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, register.class);
//                startActivity(intent);
//            }
//        });
//
//        forpass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent2 = new Intent(MainActivity.this, forget_pass.class);
//                startActivity(intent2);
//            }
//        });