package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CheckPost extends AppCompatActivity {
    RecyclerView recyclerView;
    CheckPostAdapter checkPostAdapter;
    ArrayList<ContentCheckPost> contentCheckPost = new ArrayList<>();

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
    ImageButton btnDelPost;

    ImageButton btnReward;
    ImageButton btnClose;
    ImageButton btnImpact;
    ImageButton btnIdea;
    ImageView impactfulBadge;
    ImageView ecoIdeaBadge;
    TextView impactfulCounter;
    TextView ecoIdeaCounter;
    Button btnOk;
    RelativeLayout modalView;
    RelativeLayout delPostView;
    Button btnNo;
    Button btnYes;
    ImageView notificationDot;
    RelativeLayout reportView;
    RelativeLayout utilityBarView;
    ImageButton btnClose2;
    TextView reportTv;
    TextView eventDateTv;

    SharedPreferences sharedPreferences;
    FirebaseStorage storage;


    String userId;
    String authorTxt;
    String authorId;
    String postId;
    String likeVal;
    String contentUrl;
    String postType;
    String descriptionTxt;
    Integer notifCounter = 0;
    Integer oldNotifCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_post);

        //receiving data from home activity
        authorTxt = getIntent().getStringExtra("location");
        final String authorPfp = getIntent().getStringExtra("authorPfp");
        authorId = getIntent().getStringExtra("authorId");
        final String location = getIntent().getStringExtra("commentVal");
        final String commentVal = getIntent().getStringExtra("likeVal");
        final String dateTxt = getIntent().getStringExtra("date");
        postId = getIntent().getStringExtra("postId");


        topName = findViewById(R.id.authorPost);
        contentPic = findViewById(R.id.imgPost);
        likes = findViewById(R.id.likes_val);
        comments = findViewById(R.id.comments_val);
        author = findViewById(R.id.author);
        description = findViewById(R.id.descriptionTxt);
        date = findViewById(R.id.date);
        btnDelPost = findViewById(R.id.delBtn);

        btnDelPost.setVisibility(View.GONE);

        btnReward = findViewById(R.id.btnGift);
        btnClose = findViewById(R.id.btnClose);
        btnImpact = findViewById(R.id.impactBtn);
        btnIdea = findViewById(R.id.ideaBtn);
        btnOk = findViewById(R.id.btnOk);
        impactfulBadge = findViewById(R.id.impactfulBadge);
        impactfulCounter = findViewById(R.id.impactfulCounter);
        ecoIdeaBadge = findViewById(R.id.ecoIdeaBadge);
        ecoIdeaCounter = findViewById(R.id.ecoIdeaCounter);
        modalView = findViewById(R.id.modalView);
        eventDateTv = findViewById(R.id.dateText);

        reportView = findViewById(R.id.reportView);
        btnClose2 = findViewById(R.id.btnClose2);
        reportTv = findViewById(R.id.reportTv);
        utilityBarView = findViewById(R.id.utilityBarView);

        delPostView = findViewById(R.id.delPostView);
        btnNo = findViewById(R.id.btnNo);
        btnYes = findViewById(R.id.btnYes);
        delPostView.setVisibility(View.GONE);

        notificationDot = findViewById(R.id.notificationDot);
        notificationDot.setVisibility(View.GONE);

        reportView.setVisibility(View.GONE);

        sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");

        if (userId.equals(authorId)) {
            btnReward.setVisibility(View.GONE);
            btnDelPost.setVisibility(View.VISIBLE);

        } else {
            btnReward.setVisibility(View.VISIBLE);
        }

        GLOBALS globals = (GLOBALS) getApplicationContext();
        oldNotifCounter = globals.getOldNotifCounter();
        checkForNotifs();

        Log.d("USERIDCP", userId);
        Log.d("AUTHORIDCP", authorId);
        Log.d("POSTIDCP", postId);

        //Get contentURL
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(authorId).collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        contentUrl = document.getString("contentUrl");
                        descriptionTxt = document.getString("description");

                        postType = document.getString("postType");

                        if (postType.equals("event")) {
                            eventDateTv.setVisibility(View.VISIBLE);
                            String eventDate = document.getString("eventDate");
                            eventDateTv.setText("Event Date: " + eventDate);
                        } else {
                            eventDateTv.setVisibility(View.GONE);
                        }


                    } else {
                        Log.d("TAG", "No such document");
                        String message = "This post no longer exists!";
                        Context context = getApplicationContext();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, message, duration);
                        toast.show();
                        finish();
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        //Listen for new notifications
        db.collection("users").document(userId).collection("notifications")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("ERROR", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d("NOTIFY", "New notification: " + dc.getDocument().getData());

                                    if (dc.getType().equals(DocumentChange.Type.ADDED)) {

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

        //Post deletion
        btnDelPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                delPostView.setVisibility(View.VISIBLE);

            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delPostView.setVisibility(View.GONE);
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delPostView.setVisibility(View.GONE);
                deletePost();
            }
        });

        //navigate to user profile
        author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(), CheckUser.class);
                i.putExtra("bio", authorId);
                v.getContext().startActivity(i);

            }
        });

        //Get profile Data
        db.collection("users").document(authorId).collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        final String authorTxt = document.getString("author");
                        final String authorPfp = document.getString("authorPfp");
                        final String location = document.getString("location");
                        likeVal = document.getString("likeVal");
                        final String commentVal = document.getString("commentVal");
                        final String dateTxt = document.getString("date");
                        final String descriptionTxt = document.getString("description");
                        final String contentUrl = document.getString("contentUrl");

                        topName.setText(authorTxt + "'s Post");
                        author.setText(authorTxt);
                        Picasso.get().load(contentUrl).into(contentPic);
                        description.setText(descriptionTxt);
                        date.setText(dateTxt);

                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        modalView.setVisibility(View.INVISIBLE);
        impactfulBadge.setVisibility(View.INVISIBLE);
        ecoIdeaBadge.setVisibility(View.INVISIBLE);

        //Get post Data (likes/comments) in real time
        db.collection("users").document(authorId).collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

        likeBtn = findViewById(R.id.likeBtn);
        saveBtn = findViewById(R.id.saveBtn);

        //Change likeBtn accordingly if the user already liked the post or not
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

        //Change favBtn accordingly if the user already added the post or not
        db.collection("users").document(userId).collection("favorites").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        saveBtn.setImageResource(R.drawable.fav_green);


                    } else {

                        saveBtn.setImageResource(R.drawable.fav);

                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        //display/hide badges if the post has awards for each one
        db.collection("users").document(authorId).collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String impactfulVal = document.getString("impactful");
                        String ecoIdeaVal = document.getString("ecoIdea");

                        //Impactful reward display
                        if (impactfulVal.equals("0")) {

                            impactfulBadge.setVisibility(View.GONE);
                            impactfulCounter.setVisibility(View.GONE);

                        } else {

                            impactfulBadge.setVisibility(View.VISIBLE);
                            impactfulCounter.setVisibility(View.VISIBLE);
                            impactfulCounter.setText(impactfulVal);
                        }

                        //EcoIdea reward display
                        if (ecoIdeaVal.equals("0")) {

                            ecoIdeaBadge.setVisibility(View.GONE);
                            ecoIdeaCounter.setVisibility(View.GONE);

                        } else {

                            ecoIdeaBadge.setVisibility(View.VISIBLE);
                            ecoIdeaCounter.setVisibility(View.VISIBLE);
                            ecoIdeaCounter.setText(ecoIdeaVal);
                        }


                    } else {
                        Log.d("TAG", "No such document");
                    }


                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });


        //Reward selection logic (try with Switch/case later!)
        btnReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //display/hide badges if the post has awards for each one
                db.collection("users").document(userId).collection("rewarded").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                String message = "You already rewarded this post.";
                                Context context = getApplicationContext();
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, message, duration);
                                toast.show();


                            } else {
                                Log.d("TAG", "No such document");

                                modalView.setVisibility(View.VISIBLE);

                            }


                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });

            }
        });

        btnIdea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnImpact.setBackgroundResource(R.drawable.impactful_white);
                btnIdea.setBackgroundResource(R.drawable.ecoidea_green);
                String reward = "IDEA";

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("REWARD", reward);

                        db.collection("users").document(authorId).collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {

                                        String ideaVal = document.getString("ecoIdea");
                                        Integer newIdeaVal = Integer.parseInt(ideaVal) + 1;
                                        String newIdeaValTxt = String.valueOf(newIdeaVal);

                                        ecoIdeaCounter.setText(newIdeaValTxt);
                                        ecoIdeaBadge.setVisibility(View.VISIBLE);
                                        ecoIdeaCounter.setVisibility(View.VISIBLE);

                                        Map<String, Object> rewardData = new HashMap<>();
                                        rewardData.put("ecoIdea", newIdeaValTxt);

                                        db.collection("users").document(authorId).collection("posts").document(postId).update(rewardData);
                                        db.collection("users").document(authorId).update(rewardData);

                                        //add postId to our rewarded posts
                                        Map<String, Object> postRewardData = new HashMap<>();
                                        postRewardData.put("author", authorTxt);
                                        postRewardData.put("postId", postId);

                                        db.collection("users").document(userId).collection("rewarded").document(postId).set(postRewardData);

                                        //generate notification
                                        db.collection("users").document(userId).collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {

                                                        String name = document.getString("name");
                                                        String pfpUrl = document.getString("pfp");
                                                        String contentTxt = name + " rewarded your post with: Eco Idea";
                                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                                                        Date date = new Date();
                                                        String dateTxt = formatter.format(date).toString();
                                                        String notifId = UUID.randomUUID().toString();


                                                        Map<String, Object> dataNotif = new HashMap<>();
                                                        dataNotif.put("username", name);
                                                        dataNotif.put("pfpUrl", pfpUrl);
                                                        dataNotif.put("contentUrl", contentUrl);
                                                        dataNotif.put("commentVal", contentTxt);
                                                        dataNotif.put("date", dateTxt);
                                                        dataNotif.put("notifId", notifId);
                                                        dataNotif.put("postId", postId);
                                                        dataNotif.put("authorId", authorId);

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


                                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                    } else {
                                        Log.d("TAG", "No such document");
                                    }
                                } else {
                                    Log.d("TAG", "get failed with ", task.getException());
                                }
                            }
                        });

                        modalView.setVisibility(View.INVISIBLE);
                        Snackbar.make(view, "Award given!", Snackbar.LENGTH_SHORT).show();

                    }
                });
            }
        });

        btnImpact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnImpact.setBackgroundResource(R.drawable.impactful_green);
                btnIdea.setBackgroundResource(R.drawable.ecoidea_white);
                String reward = "IMAPCTFUL";

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("REWARD", reward);

                        db.collection("users").document(authorId).collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {

                                        String impactfulVal = document.getString("impactful");
                                        Integer newImpactfulVal = Integer.parseInt(impactfulVal) + 1;
                                        String newImpactfulValTxt = String.valueOf(newImpactfulVal);

                                        impactfulCounter.setText(newImpactfulValTxt);
                                        impactfulCounter.setVisibility(View.VISIBLE);
                                        impactfulBadge.setVisibility(View.VISIBLE);

                                        Map<String, Object> rewardData = new HashMap<>();
                                        rewardData.put("impactful", newImpactfulValTxt);

                                        db.collection("users").document(authorId).collection("posts").document(postId).update(rewardData);
                                        db.collection("users").document(authorId).update(rewardData);

                                        //add postId to our rewarded posts
                                        Map<String, Object> postRewardData = new HashMap<>();
                                        postRewardData.put("author", authorTxt);
                                        postRewardData.put("postId", postId);

                                        db.collection("users").document(userId).collection("rewarded").document(postId).set(postRewardData);

                                        //generate notification
                                        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {

                                                        String name = document.getString("name");
                                                        String pfpUrl = document.getString("pfp");
                                                        String contentTxt = name + " rewarded your post with: Impactful";
                                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                                                        Date date = new Date();
                                                        String dateTxt = formatter.format(date).toString();
                                                        String notifId = UUID.randomUUID().toString();


                                                        Map<String, Object> dataNotif = new HashMap<>();
                                                        dataNotif.put("username", name);
                                                        dataNotif.put("pfpUrl", pfpUrl);
                                                        dataNotif.put("contentUrl", contentUrl);
                                                        dataNotif.put("commentVal", contentTxt);
                                                        dataNotif.put("date", dateTxt);
                                                        dataNotif.put("notifId", notifId);
                                                        dataNotif.put("postId", postId);
                                                        dataNotif.put("authorId", authorId);

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


                                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                    } else {
                                        Log.d("TAG", "No such document");
                                    }
                                } else {
                                    Log.d("TAG", "get failed with ", task.getException());
                                }
                            }
                        });

                        modalView.setVisibility(View.INVISIBLE);
                        Snackbar.make(view, "Award given!", Snackbar.LENGTH_SHORT).show();


                    }


                });
            }
        });


        contentPic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                reportView.setVisibility(View.VISIBLE);

                return false;
            }
        });

        reportTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> reportdata = new HashMap<>();
                reportdata.put("postId", postId);
                reportdata.put("contentUrl", contentUrl);
                reportdata.put("description", descriptionTxt);
                reportdata.put("authorId", authorId);

                db.collection("reports").document(postId).set(reportdata);

                reportView.setVisibility(View.GONE);
                Snackbar.make(v, "Post reported!", Snackbar.LENGTH_SHORT).show();

            }
        });

        btnClose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reportView.setVisibility(View.GONE);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modalView.setVisibility(View.INVISIBLE);
            }
        });


        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("BTNLIKE", "Click on like Btnnn");


                //Give/Remove like depending on the case
                db.collection("users").document(userId).collection("likes").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                //changing the like val on the txt view
                                likeBtn.setImageResource(R.drawable.leaf);
                                Integer dislikeVal = Integer.parseInt(likes.getText().toString()) - 1;
                                String newDislikeVal = String.valueOf(dislikeVal);
                                likes.setText(newDislikeVal);

                                //removing like from post
                                db.collection("users").document(authorId).collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

                                                db.collection("users").document(authorId).collection("posts").document(postId).update(likeData);
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

                                //deleting post from the user's liked collection
                                db.collection("users").document(userId).collection("likes").document(postId).delete();


                            } else {

                                //pretty much do the opposite to like a post
                                //Change the like value on the txt View
                                likeBtn.setImageResource(R.drawable.leaf_green);
                                Integer newLikesVal = Integer.parseInt(likes.getText().toString()) + 1;
                                String newLikesValTxt = String.valueOf(newLikesVal);
                                likes.setText(newLikesValTxt);

                                //update the like value on the database
                                db.collection("users").document(authorId).collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

                                                db.collection("users").document(authorId).collection("posts").document(postId).update(likeData);
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

                                //add the post to the user's liked collection
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

                                                //Generate and send notification data
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
                                                dataNotif.put("postId", postId);
                                                dataNotif.put("authorId", authorId);

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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add the post to the favorites or remove it
                db.collection("users").document(userId).collection("favorites").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                db.collection("users").document(userId).collection("favorites").document(postId).delete();
                                saveBtn.setImageResource(R.drawable.fav);
                                Snackbar.make(view, "Removed Post from Favorites.", Snackbar.LENGTH_SHORT).show();

                                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("TAG", "No such document");


                                db.collection("users").document(authorId).collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {

                                                String author = document.getString("author");
                                                String authorPfp = document.getString("authorPfp");
                                                String commentVal = document.getString("commentVal");
                                                String likesVal = document.getString("likeVal");
                                                String contentUrl = document.getString("contentUrl");
                                                String date = document.getString("date");
                                                String description = document.getString("description");
                                                String ecoIdea = document.getString("ecoIdea");
                                                String eventDate = document.getString("eventDate");
                                                String eventTime = document.getString("eventTime");
                                                String impactful = document.getString("impactful");
                                                String location = document.getString("location");

                                                Map<String, Object> dataFav = new HashMap<>();
                                                dataFav.put("author", author);
                                                dataFav.put("authorId", authorId);
                                                dataFav.put("authorPfp", authorPfp);
                                                dataFav.put("commentVal", commentVal);
                                                dataFav.put("likeVal", likesVal);
                                                dataFav.put("contentUrl", contentUrl);
                                                dataFav.put("date", date);
                                                dataFav.put("description", description);
                                                dataFav.put("ecoIdea", ecoIdea);
                                                dataFav.put("eventDate", eventDate);
                                                dataFav.put("eventTime", eventTime);
                                                dataFav.put("impactful", impactful);
                                                dataFav.put("location", location);
                                                dataFav.put("postId", postId);
                                                dataFav.put("postType", postType);

                                                db.collection("users").document(userId).collection("favorites").document(postId).set(dataFav);
                                                saveBtn.setImageResource(R.drawable.fav_green);
                                                Snackbar.make(view, "Added Post to Favorites!", Snackbar.LENGTH_SHORT).show();

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

                                contentCheckPost.add(new ContentCheckPost(author, authorPfp, authorId, commentId, commentVal));

                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                        RecyclerCall();

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

        commentBtn = findViewById(R.id.commentBtn);

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Comments.class);
                intent.putExtra("postId", postId);
                intent.putExtra("authorId", authorId);
                intent.putExtra("postAuthor", authorTxt);
                intent.putExtra("postAuthorPfp", authorPfp);
                intent.putExtra("description", descriptionTxt);
                startActivity(intent);
            }
        });

        Log.d("USERID_POSTID", userId + " " + postId);


    }

    private void deletePost() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("posts").document(postId).delete();

        if (postType.equals("event")) {

            db.collection("events").document(postId).delete();
        }

        // Create a storage reference from our app
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a reference to the file to delete
        StorageReference userImagesRef = storageRef.child("images/" + userId + "/" + postId);


        // Delete user image stored files
        userImagesRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d("SUCCESS", "Success deleting image file!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Log.d("ERROR STORAGE", exception.toString());

            }
        });


        String message = "Your post has been deleted!";
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();

        finish();

    }

    private void RecyclerCall() {

        recyclerView = findViewById(R.id.rvPost);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkPostAdapter = new CheckPostAdapter(this, contentCheckPost);
        recyclerView.setAdapter(checkPostAdapter);

    }

    private void checkForNotifs() {


        notificationDot = findViewById(R.id.notificationDot);

        if (oldNotifCounter != null) {
            Log.d("OldNotif", oldNotifCounter.toString());

        }

        if (!notifCounter.equals(oldNotifCounter) && notifCounter > 0) {

            notificationDot.setVisibility(View.VISIBLE);

        } else {
            notificationDot.setVisibility(View.GONE);

        }
    }
}