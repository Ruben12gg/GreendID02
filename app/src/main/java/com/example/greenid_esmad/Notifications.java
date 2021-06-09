package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Notifications extends AppCompatActivity {

    RecyclerView recyclerView;
    NotificationsAdapter notificationsAdapter;
    ArrayList<ContentNotifications> contentNotifications = new ArrayList<>();
    ImageButton delBtn;
    ImageView emptyImg;
    TextView emptyTxt;

    Integer notifCounter = 0;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        emptyImg = findViewById(R.id.emptyImg);
        emptyTxt = findViewById(R.id.emptyTxt);

        emptyImg.setVisibility(View.VISIBLE);
        emptyTxt.setVisibility(View.VISIBLE);

        sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");

        GLOBALS globals = (GLOBALS) getApplicationContext();

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


        contentNotifications.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                                    notifCounter++;
                                    Log.d("NOTIFCOUNTER", notifCounter.toString());
                                    globals.setOldNotifCounter(notifCounter);
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

        //Get notifs
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

                db.collection("users").document(userId).collection("notifications")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("USER POSTS", document.getId() + " => " + document.getData());

                                        String notifId = document.getString("notifId");

                                        sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE);
                                        String userId = sharedPreferences.getString("userId", "");

                                        db.collection("users").document(userId).collection("notifications").document(notifId).delete();

                                    }
                                } else {
                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                }
                            }

                        });
                //clear notifs
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("CLEAR", "Cleared Notifs");
                        RefreshNotifs();
                    }
                }, 180);

            }
        });


        List<String> NotifArray = new ArrayList<String>();
        db.collection("users").document(userId).collection("notifications")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("NOTIFS", document.getId() + " => " + document.getData());

                                String id = document.getString("notifId");
                                NotifArray.add(id);

                                Integer arraySize = NotifArray.size();
                                if (arraySize.equals(0)) {
                                    emptyImg.setVisibility(View.VISIBLE);
                                    emptyTxt.setVisibility(View.VISIBLE);
                                } else {
                                    emptyImg.setVisibility(View.INVISIBLE);
                                    emptyTxt.setVisibility(View.INVISIBLE);
                                }

                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                        RecyclerCall();

                    }
                });


    }

    private void RefreshNotifs() {

        recyclerView = findViewById(R.id.notificationrv);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_right);
        recyclerView.startAnimation(animation);

        contentNotifications.clear();

        //Access user Id from GLOBALS
        sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");

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
        emptyImg.setVisibility(View.VISIBLE);
        emptyTxt.setVisibility(View.VISIBLE);
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
