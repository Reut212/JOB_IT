package com.example.myapplication.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.Model.Data;
import com.example.myapplication.Adapters.adapterClassEmployer;
import com.example.myapplication.insertEmployerPost;
import com.example.myapplication.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostFragment extends Fragment {

    private FloatingActionButton fabBtn;

    private FirebaseAuth fauth;
    private DatabaseReference ref;
    ArrayList<Data> dataList;
    RecyclerView recyclerView;
    SearchView searchView;
    com.example.myapplication.Adapters.adapterClassEmployer adapterClassEmployer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);

        //Present date by specific format
        @SuppressLint("SimpleDateFormat") SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        String now = ISO_8601_FORMAT.format(new Date());

        fauth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fauth.getCurrentUser();
        assert fUser != null;
        String uID = fUser.getUid();

        fabBtn = rootView.findViewById(R.id.post_add);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity().getApplicationContext(), insertEmployerPost.class));
            }
        });

        ref = FirebaseDatabase.getInstance().getReference().child("PostJobInfo").child(uID);

        dataList = new ArrayList<Data>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterClassEmployer = new adapterClassEmployer(requireActivity().getApplicationContext(),dataList);
        recyclerView.setAdapter(adapterClassEmployer);

        searchView = rootView.findViewById(R.id.searchView1);
        return rootView;
    }


    @Override
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
                                if (counter == 4) {
                                    dataList.add(ds.getValue(Data.class));
                                }
                                counter++;
                            }
                            counter = 0;
                        }

                        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
                        adapterClassEmployer adapterClassEmployer = new adapterClassEmployer(dataList);
                        recyclerView.setAdapter(adapterClassEmployer);

                    }
                }

                ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        dataList.remove(viewHolder.getAdapterPosition());
                        ref.removeValue();
                        adapterClassEmployer.notifyDataSetChanged();
                    }
                };
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
        if(!str.equals("")) {
            ArrayList<Data> dList = new ArrayList<>();
            for (Data object : dataList) {
                if (object.getTitle().toLowerCase().contains(str.toLowerCase())) {
                    dList.add(object);
                }
            }
            adapterClassEmployer adapterClassEmployer = new adapterClassEmployer(dList);
            recyclerView.setAdapter(adapterClassEmployer);
        }else{
            adapterClassEmployer adapterClassEmployer = new adapterClassEmployer(dataList);
            recyclerView.setAdapter(adapterClassEmployer);
        }
    }


}