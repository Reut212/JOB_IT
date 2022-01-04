package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.Model.Data;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class detailedJobData extends AppCompatActivity {

    private FirebaseAuth fauth;
    private DatabaseReference ref;
    private Button goToChat;
    EditText email1;
    ArrayList<Data> dataList;
    RecyclerView recyclerView;
    com.example.myapplication.Adapters.adapterClass adapterClass;
    ArrayList<Data> arrayListCopy;

    private static final String TAG = "TAG";

    private CircleImageView postImage;

    private TextView dTitle;
    private TextView dDate;
    private TextView dLocation;
    private TextView dSalary;
    private TextView dEmail;
    private TextView dContentName;
    private TextView dJobDate;
    private TextView dDescription;

    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_job_data);

        goToChat = (Button) findViewById(R.id.button_enter_chat);
        fauth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        email1 = findViewById(R.id.emailEditText);


        dTitle = (TextView) findViewById(R.id.job_detail_title);
        dDate = (TextView) findViewById(R.id.job_details_date_board);
        dDescription = (TextView) findViewById(R.id.job_details_desc);
        dJobDate = (TextView) findViewById(R.id.job_details_job_date);
        dSalary = (TextView) findViewById(R.id.job_details_salary);
        dEmail = (TextView) findViewById(R.id.job_details_email);
        dContentName = (TextView) findViewById(R.id.job_details_contact_name);
        dLocation = (TextView) findViewById(R.id.job_details_location);
        postImage = findViewById(R.id.job_detail_image);


        //Receive data from all job activity using intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        String description = intent.getStringExtra("description");
        String jobDate = intent.getStringExtra("jobDate");
        String salary = intent.getStringExtra("salary");
        String email = intent.getStringExtra("email");
        String contactName = intent.getStringExtra("contactName");
        String location = intent.getStringExtra("location");


        //Setting values
        dTitle.setText(title);
        dDate.setText(date);
        dDescription.setText(description);
        dJobDate.setText(jobDate);
        dSalary.setText(salary);
        dEmail.setText(email);
        dContentName.setText(contactName);
        dLocation.setText(location);
        String imageUrl = intent.getStringExtra("imageUrl");




        if(imageUrl != "null"){
            storageReference = FirebaseStorage.getInstance().getReference("Post Image/" + imageUrl);
            storageReference.child("Post Image/" +imageUrl);
            try {
                File localFile = File.createTempFile("temp1File" , ".jpeg");
                storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        postImage.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "failed to get image");

                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }



        goToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUser();
            }
        });


    }

    public void sendUser(){
        Intent intent = new Intent(detailedJobData.this , ChatListUsers.class);
        startActivity(intent);
    }



}