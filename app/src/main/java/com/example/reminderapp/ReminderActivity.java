package com.example.reminderapp;


import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


//this class is to add the take the reminders from the user and inserts into database
public class ReminderActivity extends AppCompatActivity {


    Button mSubmitbtn, mDatebtn, mTimebtn, uploadBtn;
    EditText mTitledit, remTitle;
    String timeTonotify;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String userId, mremTitle;




    StorageReference storageReference;
    DatabaseReference databaseReference, dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        if(mAuth.getCurrentUser()!=null){
            userId = mAuth.getCurrentUser().getUid();

        }

        uploadBtn = findViewById(R.id.uploadBtn);


        mTitledit = (EditText) findViewById(R.id.editTitle);
        mDatebtn = (Button) findViewById(R.id.btnDate);                                             //assigned all the material reference to get and set data
        mTimebtn = (Button) findViewById(R.id.btnTime);
        remTitle = findViewById(R.id.remTitle);
        mSubmitbtn = (Button) findViewById(R.id.btnSbumit);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        dbr = FirebaseDatabase.getInstance().getReferenceFromUrl("https://loginra-eafaf-default-rtdb.firebaseio.com/");



        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mremTitle = remTitle.getText().toString();
                if(mremTitle.length()==0){
                    remTitle.requestFocus();
                    remTitle.setError("Enter Title");
                }else {
                    selectFiles();
                }
            }
        });

        mTimebtn.setOnClickListener(view -> {
            selectTime();                                                                       //when we click on the choose time button it calls the select time method
        });

        //when we click on the choose date button it calls the select date method
        mDatebtn.setOnClickListener(view -> selectDate());

        mSubmitbtn.setOnClickListener(view -> {
            String title = mTitledit.getText().toString().trim();                               //access the data form the input field
            String date = mDatebtn.getText().toString().trim();                                 //access the date form the choose date button
            String time = mTimebtn.getText().toString().trim();                                 //access the time form the choose time button

            if (title.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please Enter text", Toast.LENGTH_SHORT).show();   //shows the toast if input field is empty
            } else {
                if (time.equals("time") || date.equals("date")) {                                               //shows toast if date and time are not selected
                    Toast.makeText(getApplicationContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ReminderActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                    processinsert(title, date, time);

                }
            }


        });
    }

    private void selectFiles() {

        Intent intent = new Intent();
        intent.setType("application/vnd.ms-excel");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF Files"),1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uploadFiles(data.getData());
        }
    }

    private void uploadFiles(Uri data) {



        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference reference = storageReference.child("Uploads/"+mremTitle+".xls");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while ((!uriTask.isComplete()));
                        Uri url = uriTask.getResult();
                        
                        pdfClass pdfClass = new pdfClass(url.toString());

                         if(mAuth.getCurrentUser()!=null) {
                            String remId = databaseReference.push().getKey();
                            if(remId!=null) {
                                databaseReference.child("Profiles/" + userId + "/reminders/" + remId).setValue(true);
                                dbr.child("Reminders").child(remId).child("uid").setValue(userId);
                                dbr.child("Reminders").child(remId).child("name").setValue(mremTitle);
                                dbr.child("Reminders").child(remId).child("url").setValue(pdfClass.getUrl());
                            }
                        }
                        Toast.makeText(ReminderActivity.this, "File uploaded", Toast.LENGTH_SHORT).show();


                        progressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    double progress = (100.0+ snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded:" + (int)progress + "% there");



                    }
                });
    }

    private void processinsert(String title, String date, String time) {
        String result = new dbManager(ReminderActivity.this).addreminder(title, date, time);
        //inserts the title,date,time into sql lite database
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        setAlarm(title, date, time);                                                                //calls the set alarm method to set alarm
        mTitledit.setText("");
    }

    private void selectTime() {                                                                     //this method performs the time picker task
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(ReminderActivity.this, (timePicker, i, i1) -> {
            timeTonotify = i + ":" + i1;                                                        //temp variable to store the time to set alarm
            mTimebtn.setText(FormatTime(i, i1));                                                //sets the button text as selected time
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void selectDate() {                                                                     //this method performs the date picker task
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year1, month1, day1) -> {
            mDatebtn.setText(day1 + "-" + (month1 + 1) + "-" + year1);                             //sets the selected date as test for button
        }, year, month, day);
        datePickerDialog.show();
    }

    public String FormatTime(int hour, int minute) {                                                //this method converts the time into 12hr farmat and assigns am or pm

        String time;
        time = "";
        String formattedMinute;

        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }


        if (hour == 0) {
            time = "12" + ":" + formattedMinute + " AM";
        } else if (hour < 12) {
            time = hour + ":" + formattedMinute + " AM";
        } else if (hour == 12) {
            time = "12" + ":" + formattedMinute + " PM";
        } else {
            int temp = hour - 12;
            time = temp + ":" + formattedMinute + " PM";
        }


        return time;
    }


    private void setAlarm(String text, String date, String time) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);                   //assigining alaram manager object to set alaram

        Intent intent = new Intent(getApplicationContext(), AlarmBroadcast.class);
        intent.putExtra("event", text);                                                       //sending data to alarm class to create channel and notification
        intent.putExtra("time", date);
        intent.putExtra("date", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String dateandtime = date + " " + timeTonotify;
        DateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
        try {
            Date date1 = formatter.parse(dateandtime);

            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, date1.getTime(),am.INTERVAL_FIFTEEN_MINUTES ,pendingIntent);
            Toast.makeText(getApplicationContext(), "Alarm", Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }

        Intent intentBack = new Intent(getApplicationContext(), Home2.class);                //this intent will be called once the setting alaram is completes
        intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentBack);                                                                  //navigates from adding reminder activity ot mainactivity

    }
}