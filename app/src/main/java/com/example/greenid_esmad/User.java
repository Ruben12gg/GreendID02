package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class User extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.user);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch(menuItem.getItemId()){

                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.search:
                        startActivity(new Intent(getApplicationContext(), Search.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.new_post:
                        startActivity(new Intent(getApplicationContext(), NewPost.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.user:
                        return true;

                    case R.id.notifications:
                        startActivity(new Intent(getApplicationContext(), Notifications.class));
                        overridePendingTransition(0,0);
                        return true;


                }

                return false;
            }
        });
    }
}