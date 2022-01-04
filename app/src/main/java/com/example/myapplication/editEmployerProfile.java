package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class editEmployerProfile extends AppCompatActivity {

    private static final String TAG = "TAG";

    private TextView fNameEditText , lastNameEditText , EmailEditText , changeImage;
    private Button updateBtn;
    private String fName, lName , eMail;
    private CircleImageView profileImage;

    private FirebaseAuth fAuth;
    private DocumentReference ref;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employer_profile);

        updateBtn = findViewById(R.id.updateEmployerProfileBTN);
        profileImage = findViewById(R.id.employer_profile_image_update);
        fNameEditText = findViewById(R.id.editProfEmployerFNText);
        lastNameEditText = findViewById(R.id.editProfEmployerLNText);
        EmailEditText = findViewById(R.id.editProfEmployerEmailText);
        changeImage = findViewById(R.id.changeProfileImage);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        FirebaseUser user = fAuth.getCurrentUser();
        String Uid = user.getUid();
        ref = fStore.collection("Employers").document(Uid);

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(editEmployerProfile.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .cropOval()	    		//Allow dimmed layer to have a circle inside
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    fName = task.getResult().getString("fName");
                    lName = task.getResult().getString("lName");
                    eMail = task.getResult().getString("eMail");

                    fNameEditText.setText(fName);
                    lastNameEditText.setText(lName);
                    EmailEditText.setText(eMail);
                }
            }
        });


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String newEmail = EmailEditText.getText().toString().trim();
                            String newFName = fNameEditText.getText().toString();
                            String newLName = lastNameEditText.getText().toString();

                            //checks if all fields correct
                            if (newEmail.isEmpty()) {
                                EmailEditText.setError("Email is empty!");
                                EmailEditText.requestFocus();
                                return;
                            }
                            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                                EmailEditText.setError("Enter a valid email!");
                                EmailEditText.requestFocus();
                                return;
                            }
                            if (newFName.isEmpty()) {
                                fNameEditText.setError("First name empty!");
                                fNameEditText.requestFocus();
                                return;
                            }
                            if (newLName.isEmpty()) {
                                lastNameEditText.setError("Last name empty!");
                                lastNameEditText.requestFocus();
                                return;
                            }
                            Map<String, Object> userHashMap = new HashMap<>();
                            userHashMap.put("fName", newFName);
                            userHashMap.put("lName", newLName);
                            userHashMap.put("eMail", newEmail);

                            String userID = fAuth.getCurrentUser().getUid();

                            ref.update(userHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void unused) {
                                    Log.d(TAG, "user profile is created for" + userID );
                                    Toast.makeText(editEmployerProfile.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uri = data.getData();
        profileImage.setImageURI(uri);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File...");
        progressDialog.show();

        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        String fileName = ISO_8601_FORMAT.format(new Date())+".jpeg";

        storageReference = FirebaseStorage.getInstance().getReference("Profile Image/" + fileName);

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Map<String, Object> userUrl = new HashMap<>();
                userUrl.put("url", fileName);
                ref.update(userUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(editEmployerProfile.this, "successfully uploaded", Toast.LENGTH_SHORT).show();
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                        Log.d(TAG, "failed add url to document" ); }
                });



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(editEmployerProfile.this, "failed to upload", Toast.LENGTH_SHORT).show();
                if(progressDialog.isShowing()) progressDialog.dismiss();
            }
        });



    }
}
