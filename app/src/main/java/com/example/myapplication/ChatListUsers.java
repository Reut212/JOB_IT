package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Adapters.adapterClass;
import com.example.myapplication.Adapters.adapterClassEmployee;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatListUsers extends AppCompatActivity {

    ListView userListView;
    ArrayAdapter arrayAdapter;
    ArrayList<String> users = new ArrayList<>();
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list_users);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userListView = findViewById(R.id.userListView1);

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(!dataSnapshot.child("email").getValue().toString().equals(mAuth.getCurrentUser().getEmail())){
                            if(dataSnapshot.child("email").getValue().toString().equals(adapterClassEmployee.email)){
                                Toast.makeText(ChatListUsers.this ,adapterClassEmployee.email , Toast.LENGTH_SHORT ).show();
                                if(!users.contains(dataSnapshot.child("email").getValue().toString()))
                                  users.add(dataSnapshot.child("email").getValue().toString());
                            }
                        }
                    }
                    arrayAdapter = new ArrayAdapter(ChatListUsers.this , android.R.layout.simple_list_item_1 , users);
                    userListView.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatListUsers.this , "Failed to load user !" , Toast.LENGTH_SHORT).show();
            }
        });

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChatListUsers.this , Chat.class);
                intent.putExtra("email" , users.get(position));
                startActivity(intent);
            }
        });
    }
}