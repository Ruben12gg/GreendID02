package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {
    RecyclerView recyclerView;
    SearchAdapter searchAdapter;
    ArrayList<ContentSearch> contentSearch = new ArrayList<>();
    ImageButton btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        btnSearch = findViewById(R.id.btnSearch);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set search Selected
        bottomNavigationView.setSelectedItemId(R.id.search);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch(menuItem.getItemId()){

                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.search:
                        return true;

                    case R.id.new_post:
                        startActivity(new Intent(getApplicationContext(), NewPost.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.user:
                        startActivity(new Intent(getApplicationContext(), User.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.notifications:
                        startActivity(new Intent(getApplicationContext(), Notifications.class));
                        overridePendingTransition(0,0);
                        return true;


                }

                return false;
            }
        });
        

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUsers();
            }
        });


    }

    private void getUsers() {
        //create firebase reference
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Get data from all users
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("USERS DATA", document.getId() + " => " + document.getData());

                                String profileName = document.getString("name");
                                String pfp = document.getString("pfp");
                                String date = document.getString("date");
                                String contentUrl = document.getString("contentUrl");
                                String likeVal = document.getString("likeVal");
                                String commentVal = document.getString("commentVal");
                                String location = document.getString("location");
                                String description = document.getString("description");

                                Log.d("PROFILE + PFP", profileName + " " + pfp);


                                contentSearch.add(new ContentSearch(profileName, pfp));


                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                        RecyclerCall();
                    }
                });
    }

    private void RecyclerCall() {

        recyclerView = findViewById(R.id.rvSearch);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new SearchAdapter(this, contentSearch);
        recyclerView.setAdapter(searchAdapter);

    }


}