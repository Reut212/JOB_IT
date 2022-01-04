package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class employerRegister extends AppCompatActivity {

    public static final String TAG = "TAG";
    private Button rButton;
    private EditText fName, lName, eMail, pass, cPass;
    private String userID;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_register);

        fName = findViewById(R.id.firstName);
        lName = findViewById(R.id.lastName);
        eMail = findViewById(R.id.editTextTextEmailAddress);
        pass = findViewById(R.id.password);
        cPass = findViewById(R.id.confirmPassword);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        rButton = (Button) findViewById(R.id.employerRegisterButton);

        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailText = eMail.getText().toString().trim();
                String fNameText = fName.getText().toString();
                String lNameText = lName.getText().toString();
                String passwordText = pass.getText().toString().trim();
                String conPassText = cPass.getText().toString().trim();

                //checks if all fields correct
                if (fNameText.isEmpty()) {
                    fName.setError("First name empty!");
                    fName.requestFocus();
                    return;
                }
                if (lNameText.isEmpty()) {
                    lName.setError("Last name empty!");
                    lName.requestFocus();
                    return;
                }
                if (emailText.isEmpty()) {
                    eMail.setError("Email is empty!");
                    eMail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                    eMail.setError("Enter a valid email!");
                    eMail.requestFocus();
                    return;
                }
                if (passwordText.isEmpty() || passwordText.length() < 6) {
                    pass.setError("Minimum 6 characters!");
                    pass.requestFocus();
                    return;
                }
                if (conPassText.isEmpty() || (conPassText.length() != passwordText.length())) {
                    cPass.setError("Confirm pass don't match!");
                    cPass.requestFocus();
                    return;
                }
                for (int i = 0; i < conPassText.length(); i++) {
                    if (passwordText.charAt(i) != conPassText.charAt(i)) {
                        cPass.setError("Confirm pass don't match!");
                        cPass.requestFocus();
                        return;
                    }
                }

                registerEmployer(fNameText, lNameText, emailText, passwordText);
            }
        });
    }

    private void registerEmployer(String fname, String lname, String email, String password) {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                databaseReference.child("users").child(fAuth.getCurrentUser().getUid()).child("email").setValue(eMail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(employerRegister.this, "SignUp Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(employerRegister.this , "Failure in saving data " + e.getMessage() , Toast.LENGTH_SHORT).show();
                    }
                });

                FirebaseUser fuser = fAuth.getCurrentUser();
                //send verification email
                fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(employerRegister.this, "Verification Email Has been Sent"
                                , Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure Email not sent" + e.getMessage());
                    }
                });

                Toast.makeText(employerRegister.this, "User Created", Toast.LENGTH_SHORT).show();
                userID = fAuth.getCurrentUser().getUid();
                //find the user in database
                DocumentReference documentReference = fStore.collection("Employers").document(userID);
                //put the user fields on database
                Map<String, Object> user = new HashMap<>();
                user.put("fName", fname);
                user.put("lName", lname);
                user.put("eMail", email);
                user.put("pass", password);
                user.put("url" , "null");
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "user profile is created for" + userID);
                    }
                });
                startActivity(new Intent(getApplicationContext(), logInPage.class));
            } else {
                Toast.makeText(employerRegister.this, "Error !" + task.getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToChatList(String email){


    }
}