package com.example.myapplication.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.editEmployerProfile;
import com.example.myapplication.logInPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment{

    private TextView fNameEditText , lastNameEditText , EmailEditText;
    private Button logOutBtn , editProfile;
    private String fName, lName , eMail , url;
    private FirebaseAuth fAuth;
    private DocumentReference ref;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private CircleImageView profileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        logOutBtn = rootView.findViewById(R.id.logOutBtn);
        editProfile = rootView.findViewById(R.id.editEmployerProfile);
        fNameEditText = rootView.findViewById(R.id.firstNameTextView);
        lastNameEditText = rootView.findViewById(R.id.lastNameTextView);
        EmailEditText = rootView.findViewById(R.id.EmailTextView);
        profileImage = rootView.findViewById(R.id.employer_profile_image);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        return rootView;
    }


    public void onStart() {
        super.onStart();

        FirebaseUser user = fAuth.getCurrentUser();
        String Uid = user.getUid();
        ref = fStore.collection("Employers").document(Uid);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Fetching Profile");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    fName = task.getResult().getString("fName");
                    lName = task.getResult().getString("lName");
                    eMail = task.getResult().getString("eMail");
                    url = task.getResult().getString("url");

                    fNameEditText.setText(fName);
                    lastNameEditText.setText(lName);
                    EmailEditText.setText(eMail);

                    if (url != "null"){
                        storageReference = FirebaseStorage.getInstance().getReference("Profile Image/" + url);
                        storageReference.child("Profile Image/" + url);
                        try {
                            File localFile = File.createTempFile("tempFile" , ".jpeg");
                            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    if(progressDialog.isShowing()) progressDialog.dismiss();
                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    profileImage.setImageBitmap(bitmap);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if(progressDialog.isShowing()) progressDialog.dismiss();
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                Toast.makeText(getContext() , "Log out successes!" , Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireActivity() , logInPage.class));
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity() , editEmployerProfile.class));
            }
        });

    }


}