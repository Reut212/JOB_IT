package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Model.Data;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class insertEmployerPost extends AppCompatActivity {

    private static final String TAG = "TAG";

    private EditText job_title;
    private EditText job_desc;
    private EditText job_date;
    private EditText job_salary;
    private EditText job_location;
    private EditText job_email;
    private EditText job_contact_name;
    private Button button_post_job;

    private CircleImageView postImage;

    private ProgressDialog progressDialog;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth fauth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;//for image
    private Uri uri;

    private Data userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_employer_post);


        // initilizng our edittext and button
        job_title = findViewById(R.id.insert_job_title);
        job_desc = findViewById(R.id.insert_job_desc);
        job_date = findViewById(R.id.insert_job_date);
        job_salary = findViewById(R.id.insert_job_salary);
        job_email = findViewById(R.id.insert_job_email);
        job_location = findViewById(R.id.insert_job_place);
        job_contact_name = findViewById(R.id.insert_job_conName);
        button_post_job = findViewById(R.id.new_post_btn);
        postImage = findViewById(R.id.insert_job_image);


        //Present date by specific format
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        String now = ISO_8601_FORMAT.format(new Date());


        fauth = FirebaseAuth.getInstance();

        FirebaseUser fUser = fauth.getCurrentUser();
        assert fUser != null;
        String uID = fUser.getUid();
        // below line is used to get the
        // instance of our Firebase database.
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference().child("PostJobInfo").child(uID).child(now);

        // initializing our object
        // class variable.
        userData = new Data();

        storageReference = FirebaseStorage.getInstance().getReference();


        // adding on click listener for our button.
        button_post_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Present date by specific format
                SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");

                String timeNow = ISO_8601_FORMAT.format(new Date());
                // getting text from our edittext fields.
                String title = job_title.getText().toString();
                String description = job_desc.getText().toString();
                String dateJob = job_date.getText().toString();
                String salary = job_salary.getText().toString();
                String location = job_location.getText().toString();
                String contactName = job_contact_name.getText().toString();
                String email = job_email.getText().toString();
                String date = DateFormat.getDateInstance().format(new Date());



                // below line is for checking weather the
                // edittext fields are empty or not.
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(dateJob) || TextUtils.isEmpty(salary)) {

                    // if the text fields are empty
                    // then show the below message.
                    Toast.makeText(insertEmployerPost.this, "Data is missing...", Toast.LENGTH_SHORT).show();
                } else {
                    // else call the method to add
                    // data to our database.
                    addDatatoFirebase(title,description,dateJob,salary,date,timeNow,location,contactName,email);
                    //Return to EmployerPage
                    openEmployerPage();
                }
            }
        });

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(insertEmployerPost.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .cropOval()	    		//Allow dimmed layer to have a circle inside
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });


    }

    private void addDatatoFirebase(String title, String description, String dateJob, String salary, String date, String timeNow, String location, String contactName, String email) {
        //Setting data in our object class
        userData.setTitle(title);
        userData.setDescription(description);
        userData.setDateJob(dateJob);
        userData.setSalary(salary);
        userData.setDate(date);
        userData.setTimeNow(timeNow);
        userData.setLocation(location);
        userData.setEmail(email);
        userData.setContactName(contactName);



        //adding value event listener method which is called with database reference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // inside the method of on Data change we are setting
                // our object class to our database reference.
                // data base reference will sends data to firebase.
                databaseReference.setValue(userData);

                // after adding this data we are showing toast message.
                Toast.makeText(insertEmployerPost.this, "Data as been updated", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                Toast.makeText(insertEmployerPost.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uri = data.getData();
        postImage.setImageURI(uri);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image...");
        progressDialog.show();

        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        String fileName = ISO_8601_FORMAT.format(new Date())+".jpeg";

        storageReference = FirebaseStorage.getInstance().getReference("Post Image/" + fileName);

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                userData.setImageUrl(fileName);
                if(progressDialog.isShowing()) progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "failed add url to data" );
                if(progressDialog.isShowing()) progressDialog.dismiss();
            }
        });



    }

    public void openEmployerPage(){
        Intent intent = new Intent(this , employerPage.class);
        startActivity(intent);
    }

}