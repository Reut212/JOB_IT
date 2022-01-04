package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Fragment.ChatFragment;
import com.example.myapplication.Fragment.EmployeeHomeFragment;
import com.example.myapplication.Fragment.EmployeeProfileFragment;
import com.example.myapplication.Fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class employeePage extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_page);


        bottomNavigationView = findViewById(R.id.bottom_navigation_employee);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container_employee , new EmployeeHomeFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment=null;
                switch (item.getItemId()){
                    case R.id.nav_home:
                        fragment = new EmployeeHomeFragment();
                        break;

                    case R.id.nav_chat:
                        fragment = new ChatFragment();
                        break;

                    case  R.id.nav_profile:
                        fragment = new EmployeeProfileFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.main_container_employee , fragment).commit();

                return true;
            }
        });
    }
}