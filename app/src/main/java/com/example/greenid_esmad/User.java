package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User extends AppCompatActivity {

    TextView tvName;
    TextView tvNameTop;
    TextView tvfollowersVal;
    TextView tvfollowingVal;
    TextView tvbio;
    TextView userRank;
    ImageView ivPfp;
    ImageButton settingsBtn;
    ImageButton favoritesBtn;

    UserAdapter userAdapter;
    RecyclerView recyclerView;
    ArrayList<ContentUser> contentUser = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

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


        //Access user Id from GLOBALS
        GLOBALS globalUserId = (GLOBALS) getApplicationContext();
        String userId = globalUserId.getUserIdGlobal();


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


        //Get user Post images
        db.collection("users").document(userId).collection("posts")
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

                                Log.d("IMAGES", contentUrl);

                                contentUser.add(new ContentUser(contentUrl, author, authorPfp, date, likeVal, commentVal, location, description, postId));


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

}