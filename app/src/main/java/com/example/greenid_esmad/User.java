package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class User extends AppCompatActivity {

    TextView tvName;
    TextView tvNameTop;
    TextView tvfollowersVal;
    TextView tvfollowingVal;
    TextView tvbio;
    ImageView ivPfp;
    ImageButton settingsBtn;
    ImageButton favoritesBtn;

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
        Log.d("USERID", userId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Get profile Data
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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


    }
}