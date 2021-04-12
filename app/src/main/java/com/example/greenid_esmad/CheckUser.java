package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CheckUser extends AppCompatActivity {

    TextView usernameTop;
    TextView username;
    ImageView pfp;
    TextView bio;
    TextView followers;
    TextView following;
    ImageButton btnBack;
    Button btnFollow;


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
                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });

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

    private void follow() {


    }
}