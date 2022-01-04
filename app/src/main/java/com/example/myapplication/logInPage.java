package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class logInPage extends AppCompatActivity {

    public static final String TAG = "TAG";
    private EditText eMail, pass;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private Button registerEmploerBtn , registerEmployeeBtn;
    private Button logInButton;
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        registerEmploerBtn = (Button) findViewById(R.id.btnEmployerRegister);
        registerEmployeeBtn = (Button) findViewById(R.id.btnEmployeeRegister);
        logInButton = (Button) findViewById(R.id.btnlogin);
        forgotPassword=findViewById(R.id.forgotPassword);

        eMail = findViewById(R.id.emailEditText);
        pass = findViewById(R.id.passEditText);

        registerEmploerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmployerRegisterPage();
            }
        });

        registerEmployeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmployeeRegisterPage();
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getEmail = eMail.getText().toString();
                String getPass = pass.getText().toString().trim();

                //checks if email and pass correct
                if (getEmail.isEmpty()) {
                    eMail.setError("Email is empty!");
                    eMail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {
                    eMail.setError("enter a valid email!");
                    eMail.requestFocus();
                    return;
                }
                if (getPass.isEmpty() || getPass.length() < 6) {
                    pass.setError("minimum 6 characters! ");
                    pass.requestFocus();
                    return;
                }


                fAuth.signInWithEmailAndPassword(getEmail, getPass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseUser fUser = fAuth.getCurrentUser();
                                if (fUser.isEmailVerified()) {
                                    //check if the user is employee on database
                                    CollectionReference col = fStore.collection("Employees");
                                    DocumentReference docIdRef = col.document(fUser.getUid());
                                    docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Toast.makeText(logInPage.this, "Welcome User", Toast.LENGTH_SHORT).show();
                                                    openEmployeePage();
                                                }
                                            } else {
                                                Log.d(TAG, "Failed with: ", task.getException());
                                            }
                                        }
                                    });
                                    //check  if the user is employer on database
                                    CollectionReference col1 = fStore.collection("Employers");
                                    DocumentReference docIdRef1 = col1.document(fUser.getUid());
                                    docIdRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Toast.makeText(logInPage.this, "Welcome User", Toast.LENGTH_SHORT).show();
                                                    openEmployerPage();
                                                }
                                            } else {
                                                Log.d(TAG, "Failed with: ", task.getException());
                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(logInPage.this, "Email not Verified !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(logInPage.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail=new EditText(v.getContext());
                AlertDialog.Builder PasswordResetDialog=new AlertDialog.Builder(v.getContext());
                PasswordResetDialog.setTitle("Reset Password");
                PasswordResetDialog.setMessage("enter you email to received rest link");
                PasswordResetDialog.setView(resetMail);


                PasswordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract the email and and send rest likne


                        String mail=resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(logInPage.this,"Reset link sent to you Email",Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(logInPage.this,"Error reset link is not sent"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                PasswordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the dialog
                    }
                });

                PasswordResetDialog.create().show();
            }
        });
    }

    public void openEmployeeRegisterPage(){
        Intent intent = new Intent(this , employeeRegister.class);
        startActivity(intent);
    }

    public void openEmployerRegisterPage(){
        Intent intent = new Intent(this , employerRegister.class);
        startActivity(intent);
    }


    public void openEmployeePage() {
        Intent intent = new Intent(this, employeePage.class);
        startActivity(intent);
    }

    public void openEmployerPage() {
        Intent intent = new Intent(this, employerPage.class);
        startActivity(intent);
    }
}