package com.example.myapplication.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.Model.Data;
import com.example.myapplication.R;
import com.example.myapplication.Adapters.adapterClass;
import com.example.myapplication.detailedJobData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FirebaseAuth fauth;
    private DatabaseReference ref;
    ArrayList<Data> dataList;
    RecyclerView recyclerView;
    SearchView searchView;
    com.example.myapplication.Adapters.adapterClass adapterClass;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        fauth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fauth.getCurrentUser();
        assert fUser != null;
        String uID = fUser.getUid();

        ref = FirebaseDatabase.getInstance().getReference().child("PostJobInfo");

        dataList = new ArrayList<Data>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv1);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterClass = new adapterClass(requireActivity().getApplicationContext(),dataList);
        recyclerView.setAdapter(adapterClass);

        adapterClass.setOnItemClickListener(new adapterClass.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                startActivity(new Intent(requireActivity().getApplicationContext(), detailedJobData.class));
            }
        });

        searchView = (SearchView) rootView.findViewById(R.id.searchView);

        return rootView;
    }

    public void onStart() {
        super.onStart();

        if(ref != null){
            ref.addValueEventListener(new ValueEventListener() {

                @NonNull
                @Override
                protected Object clone() throws CloneNotSupportedException {
                    return super.clone();
                }

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int counter = 0;
                    if(snapshot.exists()){
                        dataList = new ArrayList<>();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            for(DataSnapshot ds1 : ds.getChildren()) {
                                for (DataSnapshot cart : ds1.getChildren()) {
                                    if (counter == 4) {
                                        dataList.add(ds1.getValue(Data.class));
                                    }
                                    counter++;
                                }
                                counter = 0;
                            }
                        }

                        adapterClass adapterClass = new adapterClass(dataList);
                        recyclerView.setAdapter(adapterClass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
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
        ArrayList<Data> searchList = new ArrayList<>();
        if(!str.equals("")) {
            for (Data object : dataList) {
                if (object.getTitle().toLowerCase().contains(str.toLowerCase())) {
                    searchList.add(object);
                }
            }
            adapterClass adapterClass = new adapterClass(searchList);
            recyclerView.setAdapter(adapterClass);
        }else{
            adapterClass adapterClass = new adapterClass(dataList);
            recyclerView.setAdapter(adapterClass);
        }
    }

}