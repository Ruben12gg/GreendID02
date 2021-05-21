package com.example.greenid_esmad;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    ImageButton btnBack;

    String contentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        String postAuthor = getIntent().getStringExtra("postAuthor");
        String authorId = getIntent().getStringExtra("authorId");
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
        db.collection("users").document(authorId).collection("posts").document(postId).collection("comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("COMMENTS", document.getId() + " => " + document.getData());

                                String author = document.getString("author");
                                String authorPfp = document.getString("authorPfp");
                                String authorId = document.getString("authorId");
                                String commentId = document.getString("commentId");
                                String commentVal = document.getString("commentVal");

                                contentComments.add(new ContentComments(author, authorPfp, authorId, commentId, commentVal));

                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                        RecyclerCall();

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
                if (add_comment.getText().toString().equals("")) {
                    Toast.makeText(Comments.this, "The comment cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    add_comment();
                }
            }
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void add_comment() {



        Toast.makeText(Comments.this, "Comment added!", Toast.LENGTH_SHORT).show();

        //Access user Id from GLOBALS
        GLOBALS globalUserId = (GLOBALS) getApplicationContext();
        String userId = globalUserId.getUserIdGlobal();


        String authorId = getIntent().getStringExtra("authorId");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Get profile Data to use in comment data
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

                        //post the comment to db
                        db.collection("users").document(authorId).collection("posts").document(postId).collection("comments").document(comment_id).set(commentData);

                        //Generate and send notification data
                        //Get contentURL
                        db.collection("users").document(authorId).collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {

                                        contentUrl = document.getString("contentUrl");

                                        String contentTxt = name + " commented your post.";
                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                                        Date date = new Date();
                                        String dateNotifTxt = formatter.format(date).toString();
                                        String notifId = UUID.randomUUID().toString();

                                        Map<String, Object> dataNotif = new HashMap<>();
                                        dataNotif.put("username", name);
                                        dataNotif.put("pfpUrl", author_pfp);
                                        dataNotif.put("contentUrl", contentUrl);
                                        dataNotif.put("commentVal", contentTxt);
                                        dataNotif.put("date", dateNotifTxt);
                                        dataNotif.put("notifId", notifId);
                                        dataNotif.put("postId", postId);
                                        dataNotif.put("authorId", authorId);

                                        db.collection("users").document(authorId).collection("notifications").document(notifId).set(dataNotif);


                                    } else {
                                        Log.d("TAG", "No such document");
                                    }
                                } else {
                                    Log.d("TAG", "get failed with ", task.getException());
                                }
                            }
                        });




                        //clear input box after post
                        add_comment.getText().clear();

                        Log.d("POSTID", postId);
                        //Update the commentVal from that post
                        db.collection("users").document(authorId).collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {

                                        Log.d("COMMENT VAL DATA", document.getId() + " => " + document.getData());

                                        String commentVal = document.getString("commentVal");
                                        Integer newCommentVal = Integer.parseInt(commentVal) + 1;
                                        String newCommentValTxt = String.valueOf(newCommentVal);

                                        Log.d("COMMENTVAL", newCommentValTxt);

                                        Map<String, Object> commentValData = new HashMap<>();
                                        commentValData.put("commentVal", newCommentValTxt);

                                        db.collection("users").document(authorId).collection("posts").document(postId).update(commentValData);

                                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                    } else {
                                        Log.d("TAG", "No such document");
                                    }
                                } else {
                                    Log.d("TAG", "get failed with ", task.getException());
                                }
                            }
                        });

                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });


        //refresh comment section after posting comment
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RefreshComments();
            }
        }, 100);

    }

    private void RefreshComments() {

        Log.d("REFRESH", "RefreshComments");

        contentComments.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String postId = getIntent().getStringExtra("postId");
        String authorId = getIntent().getStringExtra("authorId");

        //Get comment data
        db.collection("users").document(authorId).collection("posts").document(postId).collection("comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("COMMENTS", document.getId() + " => " + document.getData());

                                String author = document.getString("author");
                                String authorPfp = document.getString("authorPfp");
                                String authorId = document.getString("authorId");
                                String commentId = document.getString("commentId");
                                String commentVal = document.getString("commentVal");

                                contentComments.add(new ContentComments(author, authorPfp, authorId, commentId, commentVal));

                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                        RecyclerCall();

                    }
                });
    }


    private void RecyclerCall() {

        recyclerView = findViewById(R.id.rvComments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsAdapter = new CommentsAdapter(this, contentComments);
        recyclerView.setAdapter(commentsAdapter);

    }
}
