package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CheckPost extends AppCompatActivity {

    ImageButton btnBack;
    TextView topName;
    ImageView contentPic;
    ImageView zoomedImg;
    TextView likes;
    TextView comments;
    TextView author;
    TextView description;
    TextView date;
    ImageButton likeBtn;
    ImageButton commentBtn;
    ImageButton saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_post);

        //receiving data from home activity
        final String authorTxt = getIntent().getStringExtra("location");
        final String authorPfp = getIntent().getStringExtra("authorPfp");
        final String authorId = getIntent().getStringExtra("authorId");
        final String location = getIntent().getStringExtra("commentVal");
        final String likeVal = getIntent().getStringExtra("author");
        final String commentVal = getIntent().getStringExtra("likeVal");
        final String dateTxt = getIntent().getStringExtra("date");
        final String descriptionTxt = getIntent().getStringExtra("description");
        final String contentUrl = getIntent().getStringExtra("contentUrl");
        final String postId = getIntent().getStringExtra("postId");

        Log.d("AUTHOR", authorTxt);
        Log.d("LOCATION", location);
        Log.d("LIKEVAL", likeVal);
        Log.d("COMMENTVAL", commentVal);
        Log.d("DATE", dateTxt);
        Log.d("DESCRIPTION", descriptionTxt);
        Log.d("CONTENTURL", contentUrl);
        Log.d("POSTID", postId);


        topName = findViewById(R.id.authorPost);
        contentPic = findViewById(R.id.imgPost);
        likes = findViewById(R.id.likes_val);
        comments = findViewById(R.id.comments_val);
        author = findViewById(R.id.author);
        description = findViewById(R.id.descriptionTxt);
        date = findViewById(R.id.date);

        topName.setText(authorTxt + "'s Post");
        author.setText(authorTxt);
        Picasso.get().load(contentUrl).into(contentPic);
        description.setText(descriptionTxt);
        date.setText(dateTxt);

        //Get post Data (likes/comments) in real time
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String likesTxt = document.getString("likeVal");
                        String commentsTxt = document.getString("commentVal");

                        likes.setText(likesTxt);
                        comments.setText(commentsTxt);

                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });


        //Access user Id from GLOBALS
        GLOBALS globalUserId = (GLOBALS) getApplicationContext();
        String userId = globalUserId.getUserIdGlobal();

        likeBtn = findViewById(R.id.likeBtn);

        //Change followBtn text accordingly if the user follows the person or not
        db.collection("users").document(userId).collection("likes").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        likeBtn.setImageResource(R.drawable.leaf_green);


                    } else {

                        likeBtn.setImageResource(R.drawable.leaf);

                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("BTNLIKE", "Click on like Btnnn");



                Log.d("USERID", userId);
                Log.d("POSTID", postId);
                Log.d("LIKEVAL", likeVal);

                db.collection("users").document(userId).collection("likes").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                likeBtn.setImageResource(R.drawable.leaf);
                                Integer dislikeVal = Integer.parseInt(likes.getText().toString()) - 1;
                                String newDislikeVal = String.valueOf(dislikeVal);
                                likes.setText(newDislikeVal);

                                db.collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {

                                                String likeVal = document.getString("likeVal");
                                                Integer newLikeVal = Integer.parseInt(likeVal) - 1;
                                                String newLikeValTxt = String.valueOf(newLikeVal);

                                                Map<String, Object> likeData = new HashMap<>();
                                                likeData.put("likeVal", newLikeValTxt);

                                                db.collection("posts").document(postId).update(likeData);
                                                db.collection("users").document(userId).collection("posts").document(postId).update(likeData);



                                                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                            } else {
                                                Log.d("TAG", "No such document");
                                            }
                                        } else {
                                            Log.d("TAG", "get failed with ", task.getException());
                                        }
                                    }
                                });

                                db.collection("users").document(userId).collection("likes").document(postId).delete();


                            } else {

                                likeBtn.setImageResource(R.drawable.leaf_green);
                                Integer newLikesVal = Integer.parseInt(likes.getText().toString()) + 1;
                                String newLikesValTxt = String.valueOf(newLikesVal);
                                likes.setText(newLikesValTxt);


                                db.collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {

                                                String likeVal = document.getString("likeVal");
                                                Integer newLikeVal = Integer.parseInt(likeVal) + 1;
                                                String newLikeValTxt = String.valueOf(newLikeVal);

                                                Map<String, Object> likeData = new HashMap<>();
                                                likeData.put("likeVal", newLikeValTxt);

                                                db.collection("posts").document(postId).update(likeData);
                                                db.collection("users").document(userId).collection("posts").document(postId).update(likeData);



                                                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                            } else {
                                                Log.d("TAG", "No such document");
                                            }
                                        } else {
                                            Log.d("TAG", "get failed with ", task.getException());
                                        }
                                    }
                                });

                                db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {


                                                Map<String, Object> data = new HashMap<>();
                                                data.put("author", authorTxt);
                                                data.put("authorPfp", authorPfp);
                                                data.put("location", location);
                                                data.put("likeVal", likeVal);
                                                data.put("commentVal", commentVal);
                                                data.put("date", dateTxt);
                                                data.put("description", descriptionTxt);
                                                data.put("contentUrl", contentUrl);
                                                data.put("postId", postId);

                                                db.collection("users").document(userId).collection("likes").document(postId).set(data);

                                                String name = document.getString("name");
                                                String pfpUrl = document.getString("pfp");
                                                String contentTxt = name + " has liked your picture!";
                                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                                                Date date = new Date();
                                                String dateNotifTxt = formatter.format(date).toString();
                                                String notifId = UUID.randomUUID().toString();


                                                Map<String, Object> dataNotif = new HashMap<>();
                                                dataNotif.put("username", name);
                                                dataNotif.put("pfpUrl", pfpUrl);
                                                dataNotif.put("contentUrl", contentUrl);
                                                dataNotif.put("commentVal", contentTxt);
                                                dataNotif.put("date", dateNotifTxt);
                                                dataNotif.put("notifId", notifId);

                                                db.collection("users").document(authorId).collection("notifications").document(notifId).set(dataNotif);


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


        // Set Home Selected
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);

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


        btnBack = findViewById(R.id.backBtn);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("BTNBACK", "CLICK ON RETURN");
                finish();

            }
        });

    }


}