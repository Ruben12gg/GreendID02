package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Followers extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    RecyclerView recyclerView;
    FollowersAdapter followersAdapter;
    ArrayList<ContentFollowers> contentFollowers = new ArrayList<>();

    String userId;
    Integer notifCounter = 0;
    Integer oldNotifCounter;

    ImageButton btnBack;
    ImageView notificationDot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        btnBack = findViewById(R.id.btnBack);

        notificationDot = findViewById(R.id.notificationDot);
        notificationDot.setVisibility(View.GONE);

        sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");

        GLOBALS globals = (GLOBALS) getApplicationContext();
        oldNotifCounter = globals.getOldNotifCounter();
        checkForNotifs();
        getUsers();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        //Listen for new notifications
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("notifications")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("ERROR", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d("NOTIFY", "New notification: " + dc.getDocument().getData());

                                    if (dc.getType().equals(DocumentChange.Type.ADDED)){

                                        notifCounter++;
                                        Log.d("NotifCounter", notifCounter.toString());

                                    }
                                    notificationDot.setVisibility(View.VISIBLE);
                                    checkForNotifs();

                                    /*SendOnNotifChannel();*/

                                    break;
                                case MODIFIED:
                                    Log.d("MODIFY", "Modified" + dc.getDocument().getData());

                                    break;
                                case REMOVED:
                                    Log.d("REMOVE", "Removed" + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set profile Selected
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.search:
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
                        startActivity(new Intent(getApplicationContext(), Notifications.class));
                        overridePendingTransition(0, 0);
                        return true;
                }

                return false;
            }
        });

    }

    private void getUsers() {

        //create firebase reference
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Get data from all users
        db.collection("users").document(userId).collection("followers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("USERS DATA", document.getId() + " => " + document.getData());

                                String profileName = document.getString("pfName");
                                String pfp = document.getString("pfp");
                                String id = document.getString("userId");

                                contentFollowers.add(new ContentFollowers(profileName, pfp, id));


                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                        RecyclerCall();
                    }
                });
    }

    private void goBack() {
        finish();
    }


    private void RecyclerCall() {

        recyclerView = findViewById(R.id.rvFollowers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        followersAdapter = new FollowersAdapter(this, contentFollowers);
        recyclerView.setAdapter(followersAdapter);

    }

    private void checkForNotifs() {


        notificationDot = findViewById(R.id.notificationDot);

        if (oldNotifCounter != null){
            Log.d("OldNotif", oldNotifCounter.toString());

        }

        if (!notifCounter.equals(oldNotifCounter) && notifCounter > 0) {

            notificationDot.setVisibility(View.VISIBLE);

        } else {
            notificationDot.setVisibility(View.GONE);

        }
    }
}