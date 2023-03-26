package com.example.reminderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicMarkableReference;

import cz.msebera.android.httpclient.Header;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class Exl_reader extends AppCompatActivity {

    RecyclerView recyclerView;
    Adapter adapter;
    AsyncHttpClient client;
    Workbook workbook;
    List<String> Title,Date,Description;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseUser user;
    File localFile = null;
    String userId;
    ArrayList<String> rem = new ArrayList<>();
    ArrayList<String> listUrl = new ArrayList<>();
    ArrayList<String> listName = new ArrayList<>();

    private DownloadManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exl_reader);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.listOfData);

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();
        if(user!=null) {
            userId = user.getUid();
        }
        DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://loginra-eafaf-default-rtdb.firebaseio.com/");

        db.child("Reminders")
                .orderByChild("uid")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            rem.add(childSnapshot.getKey());
//                            String tag = null;
//                            Log.d(tag, rem);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        db.child("Reminders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i=0; i<rem.size(); i++) {
                    listUrl.add(snapshot.child(rem.get(i)).child("url").getValue().toString());
                }
//                String tag = null;
//                Log.d(tag, );

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        db.child("Reminders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i=0; i<rem.size(); i++) {
                    listName.add(snapshot.child(rem.get(i)).child("name").getValue().toString());
                }
//                String tag = null;
//                Log.d(tag, );

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        for(int i=0; i<listName.size(); i++) {

            // Create a storage reference from our app
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://loginra-eafaf.appspot.com/");

            StorageReference islandRef = storageRef.child("Uploads/"+listName.get(i)+".xls");
            Title = new ArrayList<String>();
            Date = new ArrayList<String>();
            Description = new ArrayList<String>();

            client = new AsyncHttpClient();
            progressBar.setVisibility(View.VISIBLE);


            try {
                localFile = File.createTempFile("newFile", "xls");
            } catch (IOException e) {
                e.printStackTrace();
            }

            islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Toast.makeText(Exl_reader.this, "file got", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Exl_reader.this, "File downloaded", Toast.LENGTH_SHORT).show();
                    WorkbookSettings ws = new WorkbookSettings();
                    ws.setGCDisabled(true);
                    if (localFile != null) {
                        try {
                            workbook = workbook.getWorkbook(localFile);
                            Sheet sheet = workbook.getSheet(0);
                            for (int i = 0; i < sheet.getRows(); i++) {

                                Cell[] row = sheet.getRow(i);
                                Title.add(row[0].getContents());
                                Date.add(row[1].getContents());
                                Description.add(row[2].getContents());

                                showData();

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (BiffException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Exl_reader.this, "Download failed.", Toast.LENGTH_SHORT).show();
                }
            });
        }


        // Create a reference with an initial file path and name
//        StorageReference pathReference = storageRef.child("images/stars.jpg");

        // Create a reference to a file from a Google Cloud Storage URI
//        StorageReference gsReference = storage.getReferenceFromUrl("gs://bucket/images/stars.jpg");

        // Create a reference from an HTTPS URL
        // Note that in the URL, characters are URL escaped!
//        StorageReference httpsReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/b/bucket/o/images%20stars.jpg");
































//        String url = "https://firebasestorage.googleapis.com/v0/b/loginra-eafaf.appspot.com/o/Uploads%2F1677414483941.xls?alt=media&token=df22a9b6-feea-40b7-b0e1-cc27a81779d7";
//        String url = "https://firebasestorage.googleapis.com/v0/b/loginra-eafaf.appspot.com/o/Uploads%2F1677413733983.xls?alt=media&token=371f2d47-bbf7-4ec9-8edb-2165fefb822f";
//        String url = "https://console.firebase.google.com/u/0/project/loginra-eafaf/storage/loginra-eafaf.appspot.com/files/~2F";
////        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
////        Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/loginra-eafaf.appspot.com/o/Uploads%2F1677414483941.xls?alt=media&token=df22a9b6-feea-40b7-b0e1-cc27a81779d7");
////        DownloadManager.Request request = new DownloadManager.Request(uri);
////        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
////        long reference = manager.enqueue(request);
//
//        client.get(url, new FileAsyncHttpResponseHandler(this) {
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File l) {
//
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, File file) {
//
//            }
//        });


    }

    private void showData() {
        adapter = new Adapter(this,Title,Description,Date);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}