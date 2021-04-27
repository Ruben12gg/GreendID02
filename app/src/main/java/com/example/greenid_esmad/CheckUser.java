package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CheckUser extends AppCompatActivity {

    TextView usernameTop;
    TextView username;
    ImageView pfp;
    TextView bio;
    TextView followers;
    TextView following;
    ImageButton btnBack;
    Button btnFollow;

    CheckUserAdapter checkUserAdapter;
    RecyclerView recyclerView;
    ArrayList<ContentCheckUser> contentCheckUser = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_user);

        usernameTop = findViewById(R.id.userNameTop_Check);
        pfp = findViewById(R.id.pfp);
        username = findViewById(R.id.pfName_Check);
        bio = findViewById(R.id.bio_Check);
        followers = findViewById(R.id.followersVal_Check);
        following = findViewById(R.id.followingVal_Check);

        final String pfName = getIntent().getStringExtra("pfName");
        final String pfpTxt = getIntent().getStringExtra("pfp");
        final String bioTxt = getIntent().getStringExtra("bio");
        final String followersTxt = getIntent().getStringExtra("followers");
        final String followingTxt = getIntent().getStringExtra("following");
        final String idTxt = getIntent().getStringExtra("id");


        Log.d("PFNAME", pfName);
        Log.d("PFP", pfpTxt);
        Log.d("BIO", bioTxt);
        Log.d("FOLLOWERS", followersTxt);
        Log.d("FOLLOWING", followingTxt);
        Log.d("ID", idTxt);

        usernameTop.setText(pfName);
        username.setText(pfName);
        bio.setText(followingTxt);
        followers.setText(followersTxt);
        following.setText(idTxt);
        Picasso.get().load(pfpTxt).into(pfp);


        //Access user Id from GLOBALS
        GLOBALS globalUserId = (GLOBALS) getApplicationContext();
        String userId = globalUserId.getUserIdGlobal();

        btnFollow = findViewById(R.id.btnFollow);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Change followBtn text accordingly if the user follows the person or not
        db.collection("users").document(userId).collection("following").document(bioTxt).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        btnFollow.setText("Unfollow");
                        Drawable redBox = getResources().getDrawable(R.drawable.btnbox_red);
                        btnFollow.setBackground(redBox);


                    } else {

                        btnFollow.setText("Follow");
                        Drawable box = getResources().getDrawable(R.drawable.btnbox);
                        btnFollow.setBackground(box);

                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        //Perform a follow or unfollow depending on the case
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("BTNFOLLOW", "CLICK ON FOLLOW");

                db.collection("users").document(userId).collection("following").document(bioTxt).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                db.collection("users").document(userId).collection("following").document(bioTxt).delete();
                                btnFollow.setText("Follow");
                                Drawable box = getResources().getDrawable(R.drawable.btnbox);
                                btnFollow.setBackground(box);

                                String followersVar = followers.getText().toString();
                                Integer newFollowers = Integer.parseInt(followersVar) - 1;
                                String newFollowersTxt = String.valueOf(newFollowers);
                                followers.setText(newFollowersTxt);

                                //Change OWN user following value
                                db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {

                                                String userFollowing = document.getString("followingVal");

                                                Integer newUserFollowing = Integer.parseInt(userFollowing) - 1;
                                                String newUserFollowingTxt = String.valueOf(newUserFollowing);

                                                Map<String, Object> data = new HashMap<>();
                                                data.put("followingVal", newUserFollowingTxt);

                                                db.collection("users").document(userId).update(data);


                                                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                            } else {
                                                Log.d("TAG", "No such document");
                                            }
                                        } else {
                                            Log.d("TAG", "get failed with ", task.getException());
                                        }
                                    }
                                });

                                //Change OTHER user followers value
                                db.collection("users").document(bioTxt).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {

                                                String followers = document.getString("followersVal");

                                                Integer newFollowersVal = Integer.parseInt(followers) - 1;
                                                String newUserFollowingTxt = String.valueOf(newFollowersVal);

                                                Map<String, Object> data = new HashMap<>();
                                                data.put("followersVal", newUserFollowingTxt);

                                                db.collection("users").document(bioTxt).update(data);


                                                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                            } else {
                                                Log.d("TAG", "No such document");
                                            }
                                        } else {
                                            Log.d("TAG", "get failed with ", task.getException());
                                        }
                                    }
                                });

                                Log.d("UNFOLLOW", "User was already followed: UNFOLLOWING!");


                            } else {
                                Log.d("FOLLOW", "No user found: FOLLOWING!");
                                Map<String, Object> data = new HashMap<>();
                                data.put("pfName", pfName);
                                data.put("pfp", pfpTxt);
                                data.put("userId", bioTxt);

                                db.collection("users").document(userId).collection("following").document(bioTxt).set(data);
                                btnFollow.setText("Unfollow");
                                Drawable redBox = getResources().getDrawable(R.drawable.btnbox_red);
                                btnFollow.setBackground(redBox);

                                String followersVar = followers.getText().toString();
                                Integer newFollowers = Integer.parseInt(followersVar) + 1;
                                String newFollowersTxt = String.valueOf(newFollowers);
                                followers.setText(newFollowersTxt);

                                //Change OWN user following value
                                db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {

                                                String userFollowing = document.getString("followingVal");

                                                Integer newUserFollowing = Integer.parseInt(userFollowing) + 1;
                                                String newUserFollowingTxt = String.valueOf(newUserFollowing);

                                                Map<String, Object> data = new HashMap<>();
                                                data.put("followingVal", newUserFollowingTxt);

                                                db.collection("users").document(userId).update(data);


                                                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                            } else {
                                                Log.d("TAG", "No such document");
                                            }
                                        } else {
                                            Log.d("TAG", "get failed with ", task.getException());
                                        }
                                    }
                                });

                                //Change OTHER user followers value
                                db.collection("users").document(bioTxt).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {

                                                String followers = document.getString("followersVal");

                                                Integer newFollowersVal = Integer.parseInt(followers) + 1;
                                                String newUserFollowingTxt = String.valueOf(newFollowersVal);

                                                Map<String, Object> data = new HashMap<>();
                                                data.put("followersVal", newUserFollowingTxt);

                                                db.collection("users").document(bioTxt).update(data);


                                                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                            } else {
                                                Log.d("TAG", "No such document");
                                            }
                                        } else {
                                            Log.d("TAG", "get failed with ", task.getException());
                                        }
                                    }
                                });

                                //Send notification to followed user
                                db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {

                                                String name = document.getString("name");
                                                String pfpUrl = document.getString("pfp");
                                                String contentTxt = name + " has followed you!";
                                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                                                Date date = new Date();
                                                String dateTxt = formatter.format(date).toString();
                                                String notifId = UUID.randomUUID().toString();

                                                Map<String, Object> data = new HashMap<>();
                                                data.put("username", name);
                                                data.put("pfpUrl", pfpUrl);
                                                data.put("contentUrl", "");
                                                data.put("commentVal", contentTxt);
                                                data.put("date", dateTxt);
                                                data.put("notifId", notifId);

                                                db.collection("users").document(bioTxt).collection("notifications").document(notifId).set(data);


                                                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                            } else {
                                                Log.d("TAG", "No such document");
                                            }
                                        } else {
                                            Log.d("TAG", "get failed with ", task.getException());
                                        }
                                    }
                                });

                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });

            }
        });

        //Get user's Post images
        db.collection("users").document(bioTxt).collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("USER POSTS", document.getId() + " => " + document.getData());

                                /*String authorPfp = document.getString("authorPfp");*/
                                String contentUrl = document.getString("contentUrl");
                                /*String author = document.getString("author");
                                String date = document.getString("date");
                                String likeVal = document.getString("likeVal");
                                String commentVal = document.getString("commentVal");
                                String location = document.getString("location");
                                String description = document.getString("description");
                                String postId = document.getId();
*/
                                Log.d("IMAGES", contentUrl);

                                contentCheckUser.add(new ContentCheckUser(contentUrl));


                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                        RecyclerCall();

                    }
                });


        btnBack = findViewById(R.id.backBtn);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("BTNBACK", "CLICK ON RETURN");
                finish();

            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set search Selected
        bottomNavigationView.setSelectedItemId(R.id.search);

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

    }

    private void RecyclerCall() {

        recyclerView = findViewById(R.id.imgGridRv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        checkUserAdapter = new CheckUserAdapter(this, contentCheckUser);
        recyclerView.setAdapter(checkUserAdapter);

    }

}