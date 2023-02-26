package com.example.reminderapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exl_reader);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.listOfData);


        String url = "https://firebasestorage.googleapis.com/v0/b/loginra-eafaf.appspot.com/o/Uploads%2F1677414483941.xls?alt=media&token=df22a9b6-feea-40b7-b0e1-cc27a81779d7";
        Title = new ArrayList<String>();
        Date =new ArrayList<String>();
        Description = new ArrayList<String>();

        client = new AsyncHttpClient();
        progressBar.setVisibility(View.VISIBLE);
        client.get(url, new FileAsyncHttpResponseHandler(this) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Exl_reader.this, "Download failed.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Exl_reader.this, "File downloaded", Toast.LENGTH_SHORT).show();
                WorkbookSettings ws = new WorkbookSettings();
                ws.setGCDisabled(true);
                if(file != null){
                    try {
                        workbook = workbook.getWorkbook(file);
                        Sheet sheet = workbook.getSheet(0);
                        for(int i=0; i<sheet.getRows(); i++){

                            Cell[] row = sheet.getRow(i);
                            Title.add(row[0].getContents());
                            Date.add(row[1].getContents());
                            Description.add(row[2].getContents());

                            showData();

                        }

                    }catch(IOException e){
                        e.printStackTrace();
                    }catch(BiffException e){
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    private void showData() {
        adapter = new Adapter(this,Title,Description,Date);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}