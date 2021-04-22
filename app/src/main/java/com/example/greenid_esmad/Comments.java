package com.example.greenid_esmad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Comments extends AppCompatActivity {

    RecyclerView recyclerView;
    CommentsAdapter commentsAdapter;
    ArrayList<ContentComments> contentComments = new ArrayList<>();

    EditText add_comment;
    ImageView pfp;
    TextView post;
    TextView post_author;
    ImageView post_author_pfp;
    TextView description_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        String postAuthor = getIntent().getStringExtra("postAuthor");
        String postAuthorPfp = getIntent().getStringExtra("postAuthorPfp");
        String description = getIntent().getStringExtra("description");

        post_author = findViewById(R.id.username);
        post_author_pfp = findViewById(R.id.profile_image);
        description_view = findViewById(R.id.description);

        post_author.setText(postAuthor);
        Picasso.get().load(postAuthorPfp).into(post_author_pfp);
        description_view.setText(description);

        //Access user Id from GLOBALS
        GLOBALS globalUserId = (GLOBALS) getApplicationContext();
        String userId = globalUserId.getUserIdGlobal();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String postId = getIntent().getStringExtra("postId");

        //Get comment data
        db.collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String author = document.getString("author");
                        String authorPfp = document.getString("authorPfp");
                        String authorId = document.getString("authorId");
                        String commentId = document.getString("commentId");
                        String commentVal = document.getString("commentVal");

                        contentComments.add(new ContentComments(author, authorPfp, authorId, commentId, commentVal));

                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
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
                        String author_pfp = document.getString("pfp");
                        pfp = findViewById(R.id.pfp);
                        Picasso.get().load(author_pfp).into(pfp);

                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        add_comment = findViewById(R.id.add_comment);
        pfp = findViewById(R.id.pfp);
        post = findViewById(R.id.post);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (add_comment.getText().toString().equals("")){
                    Toast.makeText(Comments.this, "The comment cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    add_comment();
                }
            }
        });

        // getImage();
    }

    private void add_comment(){
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
                        String author_pfp = document.getString("pfp");
                        String user_id = document.getString("id");
                        String comment_id = UUID.randomUUID().toString();
                        String commentTxt = add_comment.getText().toString();

                        Map<String, Object> commentData = new HashMap<>();
                        commentData.put("author", name);
                        commentData.put("authorId", user_id);
                        commentData.put("authorPfp", author_pfp);
                        commentData.put("commentVal", commentTxt);
                        commentData.put("commentId", comment_id);

                        String postId = getIntent().getStringExtra("postId");

                        db.collection("posts").document(postId).collection("comments").document(comment_id).set(commentData);

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

    /* private void getImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addChildEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl).into(pfp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    } */

    private void RecyclerCall() {

        recyclerView = findViewById(R.id.rvComments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsAdapter = new CommentsAdapter(this, contentComments);
        recyclerView.setAdapter(commentsAdapter);

    }
}
