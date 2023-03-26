package com.example.reminderapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reminderapp.databinding.ActivityHome2Binding;
import com.google.firebase.auth.FirebaseAuth;

public class Home2 extends AppCompatActivity {

    private static final String NAME = "name";
    public static String PREFS_NAME = "Myprefsfile";
    private FirebaseAuth mAuth;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHome2Binding binding;
//    ProgressDialog dialogbox = new ProgressDialog(Home2.this);


//    DrawerLayout drawerLayout;
//    NavigationView navigationView;
//    Toolbar toolbar;
//    SharedPreferences sp1 = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
//
//    String name = sp1.getString(NAME,"");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        drawerLayout = findViewById(R.id.drawer_layout);
//        navigationView = findViewById(R.id.nav_view);
//        toolbar = findViewById(R.id.toolbar);
//
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();




        binding = ActivityHome2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome2.toolbar);
        binding.appBarHome2.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home2.this, ReminderActivity.class));
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_curr_reminders, R.id.nav_prev_reminders)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home2);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);





    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_home:
                break;
            case R.id.nav_curr_reminders:
                startActivity(new Intent(Home2.this, MainActivity2.class));
                break;
            case R.id.nav_prev_reminders:
                startActivity(new Intent(Home2.this, home.class));
                break;
            case R.id.nav_all_reminders:
                startActivity(new Intent(Home2.this, Exl_reader.class));
                break;
            case R.id.nav_logout:
                logout();
                break;
        }

        return true;
    }

    private void logout() {





        AlertDialog.Builder builder = new AlertDialog.Builder(Home2.this);
        builder.setMessage("Are you sure you want to Log out?")
                .setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            mAuth.signOut();

                            SharedPreferences sp = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putBoolean("HasLoggedIn", false);
                            editor.commit();
//                            dialogbox.setMessage("Please wait...");
//                            dialogbox.setTitle("Log Out");
//                            dialogbox.setCanceledOnTouchOutside(false);
//                            dialogbox.show();

//                        View view = null;
//                        Snackbar.make(view, "Logged out Succesfully", Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        
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







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home2);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}