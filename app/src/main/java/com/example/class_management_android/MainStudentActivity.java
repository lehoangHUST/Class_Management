package com.example.class_management_android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

public class MainStudentActivity extends AppCompatActivity {

    Intent intent;
    MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);

        // hide The Default Actionbar
        getSupportActionBar().hide();

        intent = getIntent();
        String id_student = intent.getStringExtra("StudentID");

        // Assign variable
        bottomNavigation = findViewById(R.id.bottom_navigation_student);
        // Add menu item
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_chat_24)); // Home
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_home)); // Thông tin cơ bản
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_about)); // Chat

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                // Initialize fragment
                Fragment fragment = null;
                // Check condition
                switch (item.getId()){
                    case 1:
                        // When id is 1, initialize notification fragment
                        Bundle bundle = new Bundle();
                        bundle.putString("adminID", id_student);
                        fragment = new ChatFragment();
                        fragment.setArguments(bundle);
                        break;
                    case 2:
                        // When id is 2, initialize home fragment
                        fragment = new HomeFragment();
                        break;
                    case 3:
                        // When id is 3, initialize home fragment
                        fragment = new AboutFragment();
                        break;
                }
                loadFragment(fragment);
            }


        });

        bottomNavigation.show(2, true);

//        Các thao tác xử lý khi click vào botton navigation
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item)
            {
                // Display toast
//                Toast.makeText(getApplicationContext(),"You clicked " + item.getId(), Toast.LENGTH_SHORT).show();
            }
        });
        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item)
            {
                // Display toast
//                Toast.makeText(getApplicationContext(),"You reselected " + item.getId(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadFragment(Fragment fragment)
    {
        // Replace fragment
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }
}