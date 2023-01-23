package com.example.reminderapp;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import android.app.ProgressDialog;
import android.content.Intent;

import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private static final String NAME = "name";

    private EditText phone, password;
//    BeginSignInRequest signInRequest;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
//    FirebaseAuth mAuth;
//    SignInClient oneTapClient;
private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private BeginSignInRequest signUpRequest;




    DatabaseReference dbr = FirebaseDatabase.getInstance().getReferenceFromUrl("https://loginra-eafaf-default-rtdb.firebaseio.com/");

    String mobilepattern = "[0-9]{10}";
    public static String PREFS_NAME = "Myprefsfile";
    private FirebaseAuth mAuth;

    public class profile{
        String name ;
        String email ;
        String photoUrl;
        String uid;
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


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
        ImageButton Glogin = findViewById(R.id.Glogin);


        mAuth = FirebaseAuth.getInstance();
//        oneTapClient = Identity.getSignInClient(MainActivity.this);
//        signInRequest = BeginSignInRequest.builder()
//                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true)
//                        // Your server's client ID, not your Android client ID.
//                        .setServerClientId(getString(R.string.default_web_client_id))
//                        // Only show accounts previously used to sign in.
//                        .setFilterByAuthorizedAccounts(false)
//                        .build())
//                // Automatically sign in when exactly one credential is retrieved.
//                .setAutoSelectEnabled(false)
//                .build();
//
//
//        signIn(signInRequest);
        // ...


        Glogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
// Initialize Firebase Auth
//                mAuth = FirebaseAuth.getInstance();
                oneTapClient = Identity.getSignInClient(MainActivity.this);
                signInRequest = BeginSignInRequest.builder()
                        .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                // Your server's client ID, not your Android client ID.
                                .setServerClientId(getString(R.string.default_web_client_id))
                                // Only show accounts previously used to sign in.
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                        // Automatically sign in when exactly one credential is retrieved.
                        .setAutoSelectEnabled(false)
                        .build();


                signIn(signInRequest);

                Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();






            }



        });

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

    private void signIn(BeginSignInRequest signInRequest) {

        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {

                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        signUp();
                        Toast.makeText(MainActivity.this, "Signing up", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signUp() {

        oneTapClient = Identity.getSignInClient(this);
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No Google Accounts found. Just continue presenting the signed-out UI.
                        Toast.makeText(MainActivity.this, "No accounts Found", Toast.LENGTH_SHORT).show();

                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    String username = credential.getId();
//                    String password = credential.getPassword();
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with your backend.
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                        mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

                                            updateUI(user);
                                        } else {
                                            // If sign in fails, display a message to the user.
                                           
                                            updateUI(null);
                                        }
                                    }
                                });

                    }
//                    else if (password != null) {
//                        // Got a saved username and password. Use them to authenticate
//                        // with your backend.
//
//                    }
                } catch (ApiException e) {
                    // ...
                    switch (e.getStatusCode()) {
                        case CommonStatusCodes.CANCELED:

                            // Don't re-prompt the user.
                            showOneTapUI = false;
                            break;
                        case CommonStatusCodes.NETWORK_ERROR:
                            Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();
                            // Try again or just ignore.
                            break;
                        default:
                            break;
                    }
                }
                break;
        }
    }

    private void updateUI(FirebaseUser o) {
        
        if(o!= null){

            dbr.child("Profiles").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(o.getUid())){
                        Toast.makeText(MainActivity.this, "Love u", Toast.LENGTH_SHORT).show();
                        System.out.println(o.getProviderData());

                    }else{
                        dbr.child("Profiles").child(o.getUid()).child("Name").setValue(o.getDisplayName());
                        dbr.child("Profiles").child(o.getUid()).child("Email").setValue(o.getEmail());
                        dbr.child("Profiles").child(o.getUid()).child("Photourl").setValue(o.getPhotoUrl());
                        dbr.child("Profiles").child(o.getUid()).child("Phone").setValue(o.getPhoneNumber());
                        Toast.makeText(MainActivity.this, "love", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            startActivity(new Intent(MainActivity.this, home.class));
            finishAffinity();
        }
        
    }


}





//











































