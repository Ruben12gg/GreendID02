package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Favorites extends AppCompatActivity {
    RecyclerView recyclerView;
    FavoritesAdapter favoritesAdapter;
    ArrayList<ContentFavorites> contentFavorites = new ArrayList<>();

    ImageButton btnBack;
    ImageButton btnImageTag;
    ImageButton btnEventTag;
    ImageButton btnProductTag;
    RelativeLayout noContentView;
    RelativeLayout noContentViewFiltered;
    ImageView notificationDot;

    SharedPreferences sharedPreferences;

    String userId;
    Integer notifCounter = 0;
    Integer oldNotifCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        btnImageTag = findViewById(R.id.btnImageTag);
        btnProductTag = findViewById(R.id.btnProductTag);
        btnEventTag = findViewById(R.id.btnEventTag);
        noContentView = findViewById(R.id.noContentView);

        notificationDot = findViewById(R.id.notificationDot);
        notificationDot.setVisibility(View.GONE);

        sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        GLOBALS globals = (GLOBALS) getApplicationContext();
        oldNotifCounter = globals.getOldNotifCounter();
        checkForNotifs();

        getContent();

        //Listen for post deletions
        db.collection("users").document(userId).collection("posts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@com.google.firebase.database.annotations.Nullable QuerySnapshot snapshots,
                                        @com.google.firebase.database.annotations.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("ERROR", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d("ADDED", "Added " + dc.getDocument().getData());

                                    break;
                                case MODIFIED:
                                    Log.d("MODIFY", "Modified" + dc.getDocument().getData());

                                    break;
                                case REMOVED:
                                    Log.d("REMOVE", "Removed" + dc.getDocument().getData());

                                    contentFavorites.clear();

                                    getContent();

                                    break;
                            }
                        }

                    }
                });


        btnEventTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnImageTag.setBackgroundResource(R.drawable.postd);
                btnEventTag.setBackgroundResource(R.drawable.events);
                btnProductTag.setBackgroundResource(R.drawable.productd);


                contentFavorites.clear();


                db.collection("users").document(userId).collection("favorites")
                        .whereEqualTo("postType", "event")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("USER FAVORITES", document.getId() + " => " + document.getData());
                                        String author = document.getString("author");
                                        String authorId = document.getString("authorId");
                                        String authorPfp = document.getString("authorPfp");
                                        String date = document.getString("date");
                                        String contentUrl = document.getString("contentUrl");
                                        String likeVal = document.getString("likeVal");
                                        String commentVal = document.getString("commentVal");
                                        String location = document.getString("location");
                                        String description = document.getString("description");
                                        String postId = document.getId().toString();
                                        String ecoIdea = document.getString("ecoIdea");
                                        String eventDate = document.getString("eventDate");
                                        String eventTime = document.getString("eventTime");
                                        String impactful = document.getString("impactful");
                                        String postType = document.getString("postType");


                                        contentFavorites.add(new ContentFavorites(authorPfp, author, contentUrl, likeVal, date, commentVal, location, description, postId, userId, authorId, ecoIdea, eventDate, eventTime, impactful, postType));


                                    }
                                } else {
                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                }

                                RecyclerCall();

                            }
                        });


            }
        });


        //Listen for new notifications
        db.collection("users").document(userId).collection("notifications")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@com.google.firebase.database.annotations.Nullable QuerySnapshot snapshots,
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

        btnImageTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnImageTag.setBackgroundResource(R.drawable.posts);
                btnEventTag.setBackgroundResource(R.drawable.eventd);
                btnProductTag.setBackgroundResource(R.drawable.productd);

                contentFavorites.clear();

                sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE);
                String userId = sharedPreferences.getString("userId", "");

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                List<String> FavArray = new ArrayList<String>();
                db.collection("users").document(userId).collection("favorites")
                        .whereEqualTo("postType", "image")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("USER FAVORITES", document.getId() + " => " + document.getData());
                                        String author = document.getString("author");
                                        String authorId = document.getString("authorId");
                                        String authorPfp = document.getString("authorPfp");
                                        String date = document.getString("date");
                                        String contentUrl = document.getString("contentUrl");
                                        String likeVal = document.getString("likeVal");
                                        String commentVal = document.getString("commentVal");
                                        String location = document.getString("location");
                                        String description = document.getString("description");
                                        String postId = document.getId().toString();
                                        String ecoIdea = document.getString("ecoIdea");
                                        String eventDate = document.getString("eventDate");
                                        String eventTime = document.getString("eventTime");
                                        String impactful = document.getString("impactful");
                                        String postType = document.getString("postType");

                                        contentFavorites.add(new ContentFavorites(authorPfp, author, contentUrl, likeVal, date, commentVal, location, description, postId, userId, authorId, ecoIdea, eventDate, eventTime, impactful, postType));


                                    }
                                } else {
                                    Log.d("TAG", "Error getting documents: ", task.getException());


                                }

                                RecyclerCall();

                            }
                        });


            }
        });

        btnProductTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnImageTag.setBackgroundResource(R.drawable.postd);
                btnEventTag.setBackgroundResource(R.drawable.eventd);
                btnProductTag.setBackgroundResource(R.drawable.products);


                contentFavorites.clear();

                sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE);
                String userId = sharedPreferences.getString("userId", "");

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("users").document(userId).collection("favorites")
                        .whereEqualTo("postType", "product")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("USER FAVORITES", document.getId() + " => " + document.getData());
                                        String author = document.getString("author");
                                        String authorId = document.getString("authorId");
                                        String authorPfp = document.getString("authorPfp");
                                        String date = document.getString("date");
                                        String contentUrl = document.getString("contentUrl");
                                        String likeVal = document.getString("likeVal");
                                        String commentVal = document.getString("commentVal");
                                        String location = document.getString("location");
                                        String description = document.getString("description");
                                        String postId = document.getId().toString();
                                        String ecoIdea = document.getString("ecoIdea");
                                        String eventDate = document.getString("eventDate");
                                        String eventTime = document.getString("eventTime");
                                        String impactful = document.getString("impactful");
                                        String postType = document.getString("postType");

                                        contentFavorites.add(new ContentFavorites(authorPfp, author, contentUrl, likeVal, date, commentVal, location, description, postId, userId, authorId, ecoIdea, eventDate, eventTime, impactful, postType));


                                    }
                                } else {
                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                }

                                RecyclerCall();

                            }
                        });


            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.user);

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
                        startActivity(new Intent(getApplicationContext(), Notifications.class));
                        overridePendingTransition(0, 0);
                        return true;


                }

                return false;
            }

        });


        btnBack = findViewById(R.id.btnBack);

        //return to user
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), User.class));
            }
        });

    }

    private void getContent() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("favorites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("USER FAVORITES", document.getId() + " => " + document.getData());
                                String author = document.getString("author");
                                String authorId = document.getString("authorId");
                                String authorPfp = document.getString("authorPfp");
                                String date = document.getString("date");
                                String contentUrl = document.getString("contentUrl");
                                String likeVal = document.getString("likeVal");
                                String commentVal = document.getString("commentVal");
                                String location = document.getString("location");
                                String description = document.getString("description");
                                String postId = document.getId().toString();
                                String ecoIdea = document.getString("ecoIdea");
                                String eventDate = document.getString("eventDate");
                                String eventTime = document.getString("eventTime");
                                String impactful = document.getString("impactful");
                                String postType = document.getString("postType");

                                contentFavorites.add(new ContentFavorites(authorPfp, author, contentUrl, likeVal, date, commentVal, location, description, postId, userId, authorId, ecoIdea, eventDate, eventTime, impactful, postType));


                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                        RecyclerCall();

                    }
                });
    }

    private void RecyclerCall() {

        recyclerView = findViewById(R.id.rvFavorites);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritesAdapter = new FavoritesAdapter(this, contentFavorites);
        recyclerView.setAdapter(favoritesAdapter);

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
