package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
public class ChatListActivity extends AppCompatActivity {


    ListView userListView;
    ArrayAdapter arrayAdapter;
    static ArrayList<String> users = new ArrayList<>();
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    static boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userListView = findViewById(R.id.userListView);

        Intent intent = getIntent();

        String otherEmail = intent.getStringExtra("email");
        String email = mAuth.getCurrentUser().getEmail();

        databaseReference.child("chats").child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child("sender").getValue().toString().equals(email)) {
                            users.add(dataSnapshot.child("receiver").getValue().toString());
                        }
                        if (dataSnapshot.child("receiver").getValue().toString().equals(email)) {
                            users.add(dataSnapshot.child("sender").getValue().toString());
                        }
                    }
                    for (int i = 0; i < users.size(); ++i) {
                        for (int j = 0; j < users.size(); ++j) {
                            if (j != i) {
                                if (users.get(i).equals(users.get(j))) {
                                    users.remove(j);
                                }
                            }
                        }
                    }
                    arrayAdapter = new ArrayAdapter(ChatListActivity.this, android.R.layout.simple_list_item_1, users);
                    userListView.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatListActivity.this, "Failed to load user !", Toast.LENGTH_SHORT).show();
            }
        });

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
                intent.putExtra("email", users.get(position));
                startActivity(intent);
            }
        });
    }

}