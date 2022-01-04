package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.Adapters.adapterClass;
import com.example.myapplication.Model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class searchBar extends AppCompatActivity {

    DatabaseReference ref;
    FirebaseAuth fauth;
    ArrayList<Data> dataList;
    RecyclerView recyclerView;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar);

        fauth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fauth.getCurrentUser();
        String uID = fUser.getUid();


        ref = FirebaseDatabase.getInstance().getReference().child(uID);
        recyclerView = findViewById(R.id.rv);
        searchView = findViewById(R.id.searchView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ref != null){
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        dataList = new ArrayList<>();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            dataList.add(ds.getValue(Data.class));
                        }
                        adapterClass adapterClass = new adapterClass(dataList);
                        recyclerView.setAdapter(adapterClass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(searchBar.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }
    }
    private void search(String str){
        ArrayList<Data> dList = new ArrayList<>();
        for(Data object : dataList){
            if(object.getDescription().toLowerCase().contains(str.toLowerCase())){
                dList.add(object);
            }
        }
        adapterClass adapterClass = new adapterClass(dList);
        recyclerView.setAdapter(adapterClass);
    }
}