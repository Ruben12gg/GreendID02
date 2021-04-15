package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.greenid_esmad.Home;
import com.example.greenid_esmad.NewPost;
import com.example.greenid_esmad.R;
import com.example.greenid_esmad.Search;
import com.example.greenid_esmad.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Notifications extends AppCompatActivity {

    RecyclerView recyclerView;
    NotificationsAdapter notificationsAdapter;
    ArrayList<ContentNotifications> contentNotifications = new ArrayList<>();
    ImageButton delBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        //setSupportActionBar(findViewById(R.id.main_toolbar));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.notifications);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.search:
                        startActivity(new Intent(getApplicationContext(), Search.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.new_post:
                        startActivity(new Intent(getApplicationContext(), NewPost.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.user:
                        startActivity(new Intent(getApplicationContext(), User.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.notifications:
                        return true;


                }

                return false;
            }
        });

        //Access user Id from GLOBALS
        GLOBALS globalUserId = (GLOBALS) getApplicationContext();
        String userId = globalUserId.getUserIdGlobal();
        Log.d("USERID", userId);

        contentNotifications.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).collection("notifications")
                /*.orderBy("date", Query.Direction.ASCENDING)*/ //to use later on!
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("USER POSTS", document.getId() + " => " + document.getData());

                                String authorPfp = document.getString("pfpUrl");
                                String contentUrl = document.getString("contentUrl");
                                String commentVal = document.getString("commentVal");
                                String author = document.getString("username");
                                String notifId = document.getString("notifId");

                                GLOBALS globalUserId = (GLOBALS) getApplicationContext();
                                String userId = globalUserId.getUserIdGlobal();


                                contentNotifications.add(new ContentNotifications(authorPfp, contentUrl, commentVal, author, notifId, userId));

                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                        RecyclerCall();

                    }

                });



                delBtn = findViewById(R.id.delBtn);
                delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ONRESUME", "Activity resumed");


    }

    private void DelNotifs() {

        contentNotifications.clear();

        GLOBALS globalUserId = (GLOBALS) getApplicationContext();
        String userId = globalUserId.getUserIdGlobal();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("notifications");


    }

    private void RecyclerCall() {

        recyclerView = findViewById(R.id.notificationrv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationsAdapter = new NotificationsAdapter(this, contentNotifications);
        new ItemTouchHelper(itemTouchHelperSimpleCallBack).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(notificationsAdapter);

    }

    ItemTouchHelper.SimpleCallback itemTouchHelperSimpleCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            contentNotifications.remove(viewHolder.getAdapterPosition());
            notificationsAdapter.notifyDataSetChanged();
        }
    };



}
