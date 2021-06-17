package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.internal.StringResourceValueReader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User extends AppCompatActivity {

    TextView tvName;
    TextView tvNameTop;
    TextView tvfollowersVal;
    TextView followers;
    TextView tvfollowingVal;
    TextView following;
    TextView tvbio;
    TextView userRank;
    ImageView ivPfp;
    ImageButton settingsBtn;
    ImageButton favoritesBtn;
    ImageView notificationDot;
    ImageButton btnAllTag;
    ImageButton btnImageTag;
    ImageButton btnEventTag;
    ImageButton btnProductTag;

    TextView UR;
    RelativeLayout modalView;
    ImageButton btnClose;


    SharedPreferences sharedPreferences;

    String userId;
    Integer notifCounter = 0;
    Integer oldNotifCounter;

    UserAdapter userAdapter;
    RecyclerView recyclerView;
    ArrayList<ContentUser> contentUser = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        notificationDot = findViewById(R.id.notificationDot);
        notificationDot.setVisibility(View.GONE);

        btnAllTag = findViewById(R.id.btnAllTag);
        btnImageTag = findViewById(R.id.btnImageTag);
        btnProductTag = findViewById(R.id.btnProductTag);
        btnEventTag = findViewById(R.id.btnEventTag);

        userRank = findViewById(R.id.userRank);

        GLOBALS globals = (GLOBALS) getApplicationContext();
        oldNotifCounter = globals.getOldNotifCounter();
        checkForNotifs();

        sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        modalView = findViewById(R.id.modalView);
        UR = findViewById(R.id.UR);
        btnClose = findViewById(R.id.btnClose);


        // Set user Selected
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
                        return true;

                    case R.id.notifications:
                        startActivity(new Intent(getApplicationContext(), Notifications.class));
                        overridePendingTransition(0, 0);
                        return true;


                }

                return false;
            }

        });

        userRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Scores.class));

            }
        });


        btnAllTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAllTag.setBackgroundResource(R.drawable.alls);
                btnImageTag.setBackgroundResource(R.drawable.postd);
                btnEventTag.setBackgroundResource(R.drawable.eventd);
                btnProductTag.setBackgroundResource(R.drawable.productd);

                contentUser.clear();

                //Get user Post images
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(userId).collection("posts")
                        .orderBy("date", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("USER POSTS", document.getId() + " => " + document.getData());

                                        String authorPfp = document.getString("authorPfp");
                                        String contentUrl = document.getString("contentUrl");
                                        String author = document.getString("author");
                                        String date = document.getString("date");
                                        String likeVal = document.getString("likeVal");
                                        String commentVal = document.getString("commentVal");
                                        String location = document.getString("location");
                                        String description = document.getString("description");
                                        String postId = document.getId();
                                        String userId = document.getString("authorId");
                                        String postType = document.getString("postType");

                                        Log.d("authorPfp", authorPfp);
                                        Log.d("contentUrl", contentUrl);
                                        Log.d("author", author);
                                        Log.d("date", date);
                                        Log.d("likeVal", likeVal);
                                        Log.d("commentVal", commentVal);
                                        Log.d("location", location);
                                        Log.d("description", description);
                                        Log.d("postId", postId);
                                        Log.d("authorId", userId);
                                        Log.d("postType", postType);

                                        Log.d("IMAGES", contentUrl);

                                        contentUser.add(new ContentUser(contentUrl, author, authorPfp, date, likeVal, commentVal, location, description, postId, userId, postType));


                                    }
                                } else {
                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                }

                                RecyclerCall();

                            }
                        });
            }
        });

        btnImageTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAllTag.setBackgroundResource(R.drawable.alld);
                btnImageTag.setBackgroundResource(R.drawable.posts);
                btnEventTag.setBackgroundResource(R.drawable.eventd);
                btnProductTag.setBackgroundResource(R.drawable.productd);

                contentUser.clear();

                //Get user Post images
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(userId).collection("posts")
                        .whereEqualTo("postType", "image")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("USER POSTS", document.getId() + " => " + document.getData());

                                        String authorPfp = document.getString("authorPfp");
                                        String contentUrl = document.getString("contentUrl");
                                        String author = document.getString("author");
                                        String date = document.getString("date");
                                        String likeVal = document.getString("likeVal");
                                        String commentVal = document.getString("commentVal");
                                        String location = document.getString("location");
                                        String description = document.getString("description");
                                        String postId = document.getId();
                                        String userId = document.getString("authorId");
                                        String postType = document.getString("postType");

                                        Log.d("authorPfp", authorPfp);
                                        Log.d("contentUrl", contentUrl);
                                        Log.d("author", author);
                                        Log.d("date", date);
                                        Log.d("likeVal", likeVal);
                                        Log.d("commentVal", commentVal);
                                        Log.d("location", location);
                                        Log.d("description", description);
                                        Log.d("postId", postId);
                                        Log.d("authorId", userId);
                                        Log.d("postType", postType);

                                        Log.d("IMAGES", contentUrl);

                                        contentUser.add(new ContentUser(contentUrl, author, authorPfp, date, likeVal, commentVal, location, description, postId, userId, postType));


                                    }
                                } else {
                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                }

                                RecyclerCall();

                            }
                        });
            }
        });

        btnEventTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAllTag.setBackgroundResource(R.drawable.alld);
                btnImageTag.setBackgroundResource(R.drawable.postd);
                btnEventTag.setBackgroundResource(R.drawable.events);
                btnProductTag.setBackgroundResource(R.drawable.productd);

                contentUser.clear();

                //Get user Post images
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(userId).collection("posts")
                        .whereEqualTo("postType", "event")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("USER POSTS", document.getId() + " => " + document.getData());

                                        String authorPfp = document.getString("authorPfp");
                                        String contentUrl = document.getString("contentUrl");
                                        String author = document.getString("author");
                                        String date = document.getString("date");
                                        String likeVal = document.getString("likeVal");
                                        String commentVal = document.getString("commentVal");
                                        String location = document.getString("location");
                                        String description = document.getString("description");
                                        String postId = document.getId();
                                        String userId = document.getString("authorId");
                                        String postType = document.getString("postType");

                                        Log.d("authorPfp", authorPfp);
                                        Log.d("contentUrl", contentUrl);
                                        Log.d("author", author);
                                        Log.d("date", date);
                                        Log.d("likeVal", likeVal);
                                        Log.d("commentVal", commentVal);
                                        Log.d("location", location);
                                        Log.d("description", description);
                                        Log.d("postId", postId);
                                        Log.d("authorId", userId);
                                        Log.d("postType", postType);

                                        Log.d("IMAGES", contentUrl);

                                        contentUser.add(new ContentUser(contentUrl, author, authorPfp, date, likeVal, commentVal, location, description, postId, userId, postType));


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
                btnAllTag.setBackgroundResource(R.drawable.alld);
                btnImageTag.setBackgroundResource(R.drawable.postd);
                btnEventTag.setBackgroundResource(R.drawable.eventd);
                btnProductTag.setBackgroundResource(R.drawable.products);

                contentUser.clear();

                //Get user Post images
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(userId).collection("posts")
                        .whereEqualTo("postType", "product")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("USER POSTS", document.getId() + " => " + document.getData());

                                        String authorPfp = document.getString("authorPfp");
                                        String contentUrl = document.getString("contentUrl");
                                        String author = document.getString("author");
                                        String date = document.getString("date");
                                        String likeVal = document.getString("likeVal");
                                        String commentVal = document.getString("commentVal");
                                        String location = document.getString("location");
                                        String description = document.getString("description");
                                        String postId = document.getId();
                                        String userId = document.getString("authorId");
                                        String postType = document.getString("postType");

                                        Log.d("authorPfp", authorPfp);
                                        Log.d("contentUrl", contentUrl);
                                        Log.d("author", author);
                                        Log.d("date", date);
                                        Log.d("likeVal", likeVal);
                                        Log.d("commentVal", commentVal);
                                        Log.d("location", location);
                                        Log.d("description", description);
                                        Log.d("postId", postId);
                                        Log.d("authorId", userId);
                                        Log.d("postType", postType);

                                        Log.d("IMAGES", contentUrl);

                                        contentUser.add(new ContentUser(contentUrl, author, authorPfp, date, likeVal, commentVal, location, description, postId, userId, postType));


                                    }
                                } else {
                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                }

                                RecyclerCall();

                            }
                        });
            }
        });


        settingsBtn = findViewById(R.id.settingsBtn);

        //navegação para settings
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                startActivity(new Intent(getApplicationContext(), Settings.class));
            }
        });

        favoritesBtn = findViewById(R.id.favoritesBtn);

        //navegação para os favoritos
        favoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), Favorites.class));
            }
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Get profile Data
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String name = document.getString("name");
                        tvName = findViewById(R.id.pfName);
                        tvNameTop = findViewById(R.id.userNameTop);
                        tvName.setText(name);
                        tvNameTop.setText(name);

                        String followVal = document.getString("followersVal");
                        tvfollowersVal = findViewById(R.id.followersVal);
                        tvfollowersVal.setText(followVal);

                        String followingVal = document.getString("followingVal");
                        tvfollowingVal = findViewById(R.id.followingVal);
                        tvfollowingVal.setText(followingVal);

                        String bio = document.getString("bio");
                        tvbio = findViewById(R.id.bio);
                        tvbio.setText(bio);

                        //user rank score calculations
                        String impactful = document.getString("impactful");
                        Integer impactfulMultiplier = Integer.parseInt(impactful);

                        String ecoIdea = document.getString("ecoIdea");
                        Integer ecoIdeaMultiplier = Integer.parseInt(ecoIdea);

                        Integer userRankValTotal = (15 * impactfulMultiplier) + (10 * ecoIdeaMultiplier);
                        String userRankValTotalTxt = String.valueOf(userRankValTotal);

                        userRank = findViewById(R.id.userRank);
                        userRank.setText("GreenID Score: " + userRankValTotalTxt);

                        Map<String, Object> rankData = new HashMap<>();
                        rankData.put("score", userRankValTotal);
                        db.collection("users").document(userId).update(rankData);


                        String pfpUrl = document.getString("pfp");
                        ivPfp = findViewById(R.id.pfp);
                        Picasso.get().load(pfpUrl).into(ivPfp);

                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        modalView.setVisibility(View.GONE);

        UR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modalView.setVisibility(View.VISIBLE);}});

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modalView.setVisibility(View.GONE);}});



        getPosts();


        //Navigation to followers/following
        tvfollowingVal = findViewById(R.id.followingVal);
        tvfollowingVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Following.class));
            }
        });

        tvfollowersVal = findViewById(R.id.followersVal);
        tvfollowersVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Followers.class));
            }
        });

        following = findViewById(R.id.following);
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Following.class));
            }
        });

        followers = findViewById(R.id.followers);
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Followers.class));
            }
        });

        //Listen for new notifications
        db.collection("users").document(userId).collection("notifications")
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

                                    contentUser.clear();

                                    getPosts();

                                    break;
                            }
                        }

                    }
                });


    }

    private void getPosts() {

        //Get user Post images
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("posts")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("USER POSTS", document.getId() + " => " + document.getData());

                                String authorPfp = document.getString("authorPfp");
                                String contentUrl = document.getString("contentUrl");
                                String author = document.getString("author");
                                String date = document.getString("date");
                                String likeVal = document.getString("likeVal");
                                String commentVal = document.getString("commentVal");
                                String location = document.getString("location");
                                String description = document.getString("description");
                                String postId = document.getId();
                                String userId = document.getString("authorId");
                                String postType = document.getString("postType");

                                Log.d("authorPfp", authorPfp);
                                Log.d("contentUrl", contentUrl);
                                Log.d("author", author);
                                Log.d("date", date);
                                Log.d("likeVal", likeVal);
                                Log.d("commentVal", commentVal);
                                Log.d("location", location);
                                Log.d("description", description);
                                Log.d("postId", postId);
                                Log.d("authorId", userId);
                                Log.d("postType", postType);

                                Log.d("IMAGES", contentUrl);

                                contentUser.add(new ContentUser(contentUrl, author, authorPfp, date, likeVal, commentVal, location, description, postId, userId, postType));


                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                        RecyclerCall();

                    }
                });

    }

    private void RecyclerCall() {

        recyclerView = findViewById(R.id.imgGridRv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        userAdapter = new UserAdapter(this, contentUser);
        recyclerView.setAdapter(userAdapter);

    }

    private void checkForNotifs() {

        Log.d("CHECKING", "checkForNotifs: ");

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