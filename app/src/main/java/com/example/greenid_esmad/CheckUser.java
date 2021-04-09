package com.example.greenid_esmad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class CheckUser extends AppCompatActivity {

    TextView usernameTop;
    TextView username;
    ImageView pfp;
    TextView bio;
    TextView followers;
    TextView following;
    ImageButton btnBack;


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
        btnBack = findViewById(R.id.backBtn);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("BTNBACK", "CLICK");
                finish();

            }
        });

    }
}