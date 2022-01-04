package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.myapplication.Fragment.ChatFragment;
import com.example.myapplication.Fragment.HomeFragment;
import com.example.myapplication.Fragment.PostFragment;
import com.example.myapplication.Fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class employerPage extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_page);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container , new HomeFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment=null;
                switch (item.getItemId()){
                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        break;

                    case R.id.nav_post:
                        fragment = new PostFragment();
                        break;

                    case  R.id.nav_profile:
                        fragment = new ProfileFragment();
                        break;

                    case R.id.nav_chat:
                        fragment = new ChatFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.main_container , fragment).commit();

                return true;
            }
        });



//        btnAllJob = findViewById(R.id.btn_empAllJobs);
//        btnPostJob = findViewById(R.id.btn_postJob);
//        btnAllJob.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                startActivity(new Intent(getApplicationContext(), allJobs.class));
//            }
//        });
//
//        btnPostJob.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                startActivity(new Intent(getApplicationContext(), postJob.class));
//
//            }
//        });

    }
}