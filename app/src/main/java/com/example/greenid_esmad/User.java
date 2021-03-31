package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    ImageView ivPfp;
    ImageButton settingsBtn;
    ImageButton favoritesBtn;

    GridView gridView;
    int[] imagePosts = {R.drawable.pic01, R.drawable.pic02, R.drawable.pic03, R.drawable.pic04, R.drawable.pic05, R.drawable.pic06, R.drawable.pic07, R.drawable.pic08, R.drawable.pic09, R.drawable.pic10};
    ArrayList<String> image_Posts = new ArrayList<String>();

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

        gridView = findViewById(R.id.imgGrid);

        CustomAdapter customAdapter = new CustomAdapter();
        gridView.setAdapter(customAdapter);

        settingsBtn = findViewById(R.id.settingsBtn);

        //navegação para settings
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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


        /*db.collection("users").document(userId).collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("USER POSTS", document.getId() + " => " + document.getData());

                                String imagePostUrl = document.getString("picUrl");


                                *//*final Map<String, Object> pfPosts = new HashMap<>();
                                pfPosts.put("picUrl", imagePostUrl);*//*

                                Log.d("PF POSTS", imagePostUrl);

                                image_Posts.add(imagePostUrl);
                                String imagePostsTxt = image_Posts.toString();

                                Log.d("IMAGE ARRAY", imagePostsTxt);


                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                    }
                });*/


    }

    private class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return imagePosts.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = getLayoutInflater().inflate(R.layout.row_data, null);
            //getting view in row_data
            ImageView images = view1.findViewById(R.id.imgGV);

            images.setImageResource(imagePosts[i]);


            return view1;


        }

    }

}