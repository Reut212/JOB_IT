package com.example.myapplication.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Data;
import com.example.myapplication.R;
import com.example.myapplication.Adapters.adapterClassEmployee;
import com.example.myapplication.detailedJobData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EmployeeHomeFragment extends Fragment {

    private FirebaseAuth fauth;
    private DatabaseReference ref, favoriteRef, fvrt_listRef;
    ArrayList<Data> dataList;
    RecyclerView recyclerView;
    SearchView searchView;
    com.example.myapplication.Adapters.adapterClassEmployee adapterClassEmployee;
    ImageButton save;

    Boolean fvrtChecker = false;
    Data data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_employee_home, container, false);

        fauth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fauth.getCurrentUser();
        assert fUser != null;
        String uID = fUser.getUid();

        ref = FirebaseDatabase.getInstance().getReference().child("PostJobInfo");
        data = new Data();
        favoriteRef = FirebaseDatabase.getInstance().getReference("favourites");
        fvrt_listRef = FirebaseDatabase.getInstance().getReference("favouriteList").child(uID);

        dataList = new ArrayList<Data>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv1);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterClassEmployee = new adapterClassEmployee(requireActivity().getApplicationContext(), dataList);
        recyclerView.setAdapter(adapterClassEmployee);


        adapterClassEmployee.setOnItemClickListener(new adapterClassEmployee.OnItemClickListener() {
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

        if (ref != null) {
            ref.addValueEventListener(new ValueEventListener() {

                @NonNull
                @Override
                protected Object clone() throws CloneNotSupportedException {
                    return super.clone();
                }

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int counter = 0;
                    if (snapshot.exists()) {
                        dataList = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            for (DataSnapshot ds1 : ds.getChildren()) {
                                for (DataSnapshot cart : ds1.getChildren()) {
                                    if (counter == 4) {
                                        dataList.add(ds1.getValue(Data.class));
                                    }
                                    counter++;
                                }
                                counter = 0;
                            }
                        }

                        adapterClassEmployee adapterClassEmployee = new adapterClassEmployee(dataList);
                        recyclerView.setAdapter(adapterClassEmployee);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (searchView != null) {
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

    private void search(String str) {
        ArrayList<Data> searchList = new ArrayList<>();
        if (!str.equals("")) {
            for (Data object : dataList) {
                if (object.getTitle().toLowerCase().contains(str.toLowerCase())) {
                    searchList.add(object);
                }
            }
            adapterClassEmployee adapterClassEmployee = new adapterClassEmployee(searchList);
            recyclerView.setAdapter(adapterClassEmployee);
        } else {
            adapterClassEmployee adapterClassEmployee = new adapterClassEmployee(dataList);
            recyclerView.setAdapter(adapterClassEmployee);
        }
        }

}
