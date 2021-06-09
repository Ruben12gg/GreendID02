package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    FeedAdapter feedAdapter;
    RecyclerView recyclerView;
    ArrayList<ContentFeed> feedContent = new ArrayList<>();
    TextView followerTxt;
    ImageView followerImg;
    ImageView notificationDot;
    SwipeRefreshLayout swipeRefreshLayout;

    String userId;
    Integer notifCounter = 0;
    Integer oldNotifCounter;


    private NotificationManagerCompat notificationManagerCompat;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        notificationDot = findViewById(R.id.notificationDot);
        notificationDot.setVisibility(View.GONE);

        notificationManagerCompat = NotificationManagerCompat.from(this);

        followerTxt = findViewById(R.id.textView2);
        followerImg = findViewById(R.id.imageView);

        followerTxt.setVisibility(View.INVISIBLE);
        followerImg.setVisibility(View.INVISIBLE);

        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                feedContent.clear();
                getContent();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        GLOBALS globals = (GLOBALS) getApplicationContext();
        oldNotifCounter = globals.getOldNotifCounter();

        checkForNotifs();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Get data from profile to make the logic to show/hide follow message
        sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");


        //Listen for new notifications
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

        //Get profile Data
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String followingValTxt = document.getString("followingVal");

                        int followingValNum = Integer.parseInt(followingValTxt);

                        Log.d("FOLLOWING VAL", String.valueOf(followingValNum));

                        if (followingValTxt.equals("0")) {

                            Log.d("FOLLOWING", "EQUALS 0");

                            followerTxt.setVisibility(View.VISIBLE);
                            followerImg.setVisibility(View.VISIBLE);

                        }

                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        getContent();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.home:
                        finish();
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

    private void getContent() {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).collection("following")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("FOLLOWING DOC", document.getId() + " => " + document.getData());

                                String followingId = document.getString("userId");

                                db.collection("users").document(followingId).collection("posts")
                                        .orderBy("date", Query.Direction.DESCENDING)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @RequiresApi(api = Build.VERSION_CODES.O)
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d("ACTIVITY FEED", document.getId() + " => " + document.getData());


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


                                                        feedContent.add(new ContentFeed(authorPfp, author, contentUrl, likeVal, date, commentVal, location, description, postId, userId, authorId, ecoIdea, eventDate, eventTime, impactful, postType));


                                                    }
                                                } else {
                                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                                }
                                                RecyclerCall();
                                            }
                                        });

                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }


    private void RecyclerCall() {

        recyclerView = findViewById(R.id.rvHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedAdapter = new FeedAdapter(this, feedContent);
        recyclerView.setAdapter(feedAdapter);

    }

    //Deactivated code for external notifs
    /*private void SendOnNotifChannel() {

        Intent notifIntent = new Intent(this, Notifications.class);
        //Access user Id from GLOBALS
        GLOBALS globalUserId = (GLOBALS) getApplicationContext();
        String userId = globalUserId.getUserIdGlobal();
        notifIntent.putExtra("userId", userId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notifIntent,
                0
        );

        Notification notification = new NotificationCompat.Builder(this, GLOBALS.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.logo_greenid)
                .setContentTitle("GreenID")
                .setContentText("You have new profile interactions!")
                .setContentIntent(pendingIntent)
                .build();

        notificationManagerCompat.notify(1, notification);


    }*/
}