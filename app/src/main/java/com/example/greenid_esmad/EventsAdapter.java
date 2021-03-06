package com.example.greenid_esmad;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    //criação de referencias
    private List<ContentEvents> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    EventsAdapter(Context context, ArrayList<ContentEvents> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.new_post_card, parent, false);
        return new ViewHolder(view);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvAuthor;
        TextView tvDate;
        TextView tvLikeVal;
        TextView tvLocation;
        TextView tvCommentVal;
        TextView tvDescription;
        ImageView pfp;
        ImageView contentPic;
        ImageView locationIcon;
        ImageButton btnLike;
        ImageButton btnComment;
        ImageButton btnSaved;
        ImageButton btnReward;
        ImageButton btnClose;
        ImageButton btnImpact;
        ImageButton btnIdea;
        ImageView impactfulBadge;
        ImageView ecoIdeaBadge;
        TextView impactfulCounter;
        TextView ecoIdeaCounter;
        ImageButton btn;
        Button btnOk;
        RelativeLayout modalView;
        RelativeLayout resultCard;
        RelativeLayout reportView;
        RelativeLayout utilityBarView;
        ImageButton btnClose2;
        TextView reportTv;


        ViewHolder(View itemView) {
            super(itemView);

            tvAuthor = itemView.findViewById(R.id.likes_val);
            tvLikeVal = itemView.findViewById(R.id.comments_val);
            tvCommentVal = itemView.findViewById(R.id.location);
            tvLocation = itemView.findViewById(R.id.pf_name);
            locationIcon = itemView.findViewById(R.id.location_icon);
            contentPic = itemView.findViewById(R.id.post_image);
            pfp = itemView.findViewById(R.id.pfp);
            tvDescription = itemView.findViewById(R.id.descriptionText);
            tvDate = itemView.findViewById(R.id.dateText);
            btnLike = itemView.findViewById(R.id.favorites_icon);
            btnComment = itemView.findViewById(R.id.comments_icon);
            btnSaved = itemView.findViewById(R.id.btnSaved);
            btnReward = itemView.findViewById(R.id.btnGift);
            btnClose = itemView.findViewById(R.id.btnClose);
            btnImpact = itemView.findViewById(R.id.impactBtn);
            btnIdea = itemView.findViewById(R.id.ideaBtn);
            btnOk = itemView.findViewById(R.id.btnOk);
            impactfulBadge = itemView.findViewById(R.id.impactfulBadge);
            impactfulCounter = itemView.findViewById(R.id.impactfulCounter);
            ecoIdeaBadge = itemView.findViewById(R.id.ecoIdeaBadge);
            ecoIdeaCounter = itemView.findViewById(R.id.ecoIdeaCounter);
            resultCard = itemView.findViewById(R.id.post_card_02);
            modalView = itemView.findViewById(R.id.modalView);
            reportView = itemView.findViewById(R.id.reportView);
            btnClose2 = itemView.findViewById(R.id.btnClose2);
            reportTv = itemView.findViewById(R.id.reportTv);
            utilityBarView = itemView.findViewById(R.id.utilityBarView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.modalView.setVisibility(View.GONE);
        holder.impactfulBadge.setVisibility(View.GONE);
        holder.impactfulCounter.setVisibility(View.GONE);
        holder.ecoIdeaBadge.setVisibility(View.GONE);
        holder.ecoIdeaCounter.setVisibility(View.GONE);
        holder.locationIcon.setVisibility(View.GONE);
        holder.reportView.setVisibility(View.GONE);


        ContentEvents contentEvents = mData.get(position);
        holder.tvAuthor.setText(contentEvents.getAuthor());
        holder.tvDate.setText(contentEvents.getDate());
        holder.tvLikeVal.setText(contentEvents.getLikeVal());
        holder.tvCommentVal.setText(contentEvents.getCommentVal());
        holder.tvLocation.setText(contentEvents.getLocation());

        //Cut down description Txt if it's too big to fully appear on post post
        String description = contentEvents.getDescription();
        if (description.length() >= 35) {

            String subDesc = description.substring(0, 34) + "...";
            holder.tvDescription.setText(subDesc);

        } else {
            holder.tvDescription.setText(description);
        }

        final String authorPfp = contentEvents.getAuthorPfp();
        final String contentUrl = contentEvents.getContentUrl();


        Picasso.get().load(authorPfp).into(holder.pfp);
        Picasso.get().load(contentUrl).into(holder.contentPic);

        String author = contentEvents.getAuthor();
        String authorId = contentEvents.getAuthorId();
        String location = contentEvents.getLocation();
        String likesVal = contentEvents.getLikeVal();
        String commentVal = contentEvents.getCommentVal();
        String date = contentEvents.getDate();
        String postId = contentEvents.getPostId();
        String commentId = contentEvents.getCommentId();
        String userId = contentEvents.getUserId();

        Log.d("USERIDEV", userId);
        Log.d("AUTHORIDEV", authorId);
        Log.d("POSTIDEV", postId);


        if (userId.equals(authorId)){
            holder.btnReward.setVisibility(View.GONE);
        } else {
            holder.btnReward.setVisibility(View.VISIBLE);
        }

        /*if (postType.equals("event")) {
            holder.tvDate.setText("Event Date: " + eventDate);
        } else {
            holder.tvDate.setText(date);
        }*/

        if (commentVal.isEmpty()) {
            holder.locationIcon.setVisibility(View.GONE);
        } else {
            holder.locationIcon.setVisibility(View.VISIBLE);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Change like icon depending if the user has already liked the post or not
        db.collection("users").document(userId).collection("likes").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        holder.btnLike.setImageResource(R.drawable.leaf_green);


                    } else {

                        holder.btnLike.setImageResource(R.drawable.leaf);

                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        //Change favorites icon depending if the user has already added the post or not
        db.collection("users").document(userId).collection("favorites").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        holder.btnSaved.setImageResource(R.drawable.fav_green);


                    } else {

                        holder.btnSaved.setImageResource(R.drawable.fav);

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

                            holder.impactfulBadge.setVisibility(View.GONE);
                            holder.impactfulCounter.setVisibility(View.GONE);

                        } else {

                            holder.impactfulBadge.setVisibility(View.VISIBLE);
                            holder.impactfulCounter.setVisibility(View.VISIBLE);
                            holder.impactfulCounter.setText(impactfulVal);
                        }

                        //EcoIdea reward display
                        if (ecoIdeaVal.equals("0")) {

                            holder.ecoIdeaBadge.setVisibility(View.GONE);
                            holder.ecoIdeaCounter.setVisibility(View.GONE);

                        } else {

                            holder.ecoIdeaBadge.setVisibility(View.VISIBLE);
                            holder.ecoIdeaCounter.setVisibility(View.VISIBLE);
                            holder.ecoIdeaCounter.setText(ecoIdeaVal);
                        }


                    } else {
                        Log.d("TAG", "No such document");
                    }


                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });


        holder.resultCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "XPTO");

                Intent i = new Intent(v.getContext(), CheckPost.class);
                i.putExtra("authorId", authorId);
                i.putExtra("postId", postId);
                v.getContext().startActivity(i);


            }
        });


        //navigate to user profile
        holder.tvAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(), CheckUser.class);
                i.putExtra("bio", authorId);
                v.getContext().startActivity(i);

            }
        });

        holder.pfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(), CheckUser.class);
                i.putExtra("bio", authorId);
                v.getContext().startActivity(i);

            }
        });

        holder.utilityBarView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                holder.reportView.setVisibility(View.VISIBLE);

                return false;
            }
        });

        holder.reportTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> reportdata = new HashMap<>();
                reportdata.put("postId", postId);
                reportdata.put("contentUrl", contentUrl);
                reportdata.put("description", description);
                reportdata.put("authorId", authorId);

                db.collection("reports").document(postId).set(reportdata);

                holder.reportView.setVisibility(View.GONE);
                Snackbar.make(v, "Post reported!", Snackbar.LENGTH_SHORT).show();

            }
        });

        holder.btnClose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.reportView.setVisibility(View.GONE);
            }
        });

        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BTNCOMMENT", "Click on comment Btn");

                Intent i = new Intent(v.getContext(), Comments.class);
                i.putExtra("postAuthor", location);
                i.putExtra("postAuthorPfp", authorPfp);
                i.putExtra("authorId", authorId);
                i.putExtra("commentId", commentId);
                i.putExtra("date", date);
                i.putExtra("description", description);
                i.putExtra("postId", postId);
                v.getContext().startActivity(i);
            }
        });

        holder.btnSaved.setOnClickListener(new View.OnClickListener() {
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
                                holder.btnSaved.setImageResource(R.drawable.fav);
                                Snackbar.make(view, "Removed Post from Favorites.", Snackbar.LENGTH_SHORT).show();

                                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("TAG", "No such document");

                                Map<String, Object> data = new HashMap<>();
                                data.put("author", author);
                                data.put("authorPfp", authorPfp);
                                data.put("location", location);
                                data.put("likeVal", likesVal);
                                data.put("commentVal", commentVal);
                                data.put("date", date);
                                data.put("description", description);
                                data.put("contentUrl", contentUrl);
                                data.put("postId", postId);

                                db.collection("users").document(userId).collection("favorites").document(postId).set(data);
                                holder.btnSaved.setImageResource(R.drawable.fav_green);
                                Snackbar.make(view, "Added Post to Favorites!", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });
            }
        });

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("BTNLIKE", "Click on like Btnnn");

                Log.d("USERID", userId);
                Log.d("POSTID", postId);
                Log.d("LIKEVAL", author);

                //Add/remove like from post, depending on the case
                db.collection("users").document(userId).collection("likes").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                //update value on the text View
                                holder.btnLike.setImageResource(R.drawable.leaf);
                                Integer newDislikeVal = Integer.parseInt(holder.tvAuthor.getText().toString()) - 1;
                                String newDislikeValTxt = String.valueOf(newDislikeVal);
                                holder.tvAuthor.setText(newDislikeValTxt);

                                //update like value on the database
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
                                                db.collection("events").document(postId).update(likeData);


                                                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                            } else {
                                                Log.d("TAG", "No such document");
                                            }
                                        } else {
                                            Log.d("TAG", "get failed with ", task.getException());
                                        }
                                    }
                                });

                                //Remove post from the the user's liked collection
                                db.collection("users").document(userId).collection("likes").document(postId).delete();


                            } else {

                                //Update like value on the text View
                                holder.btnLike.setImageResource(R.drawable.leaf_green);
                                Integer newLikesVal = Integer.parseInt(holder.tvAuthor.getText().toString()) + 1;
                                String newLikesValTxt = String.valueOf(newLikesVal);
                                holder.tvAuthor.setText(newLikesValTxt);

                                //update like and add to database
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
                                                db.collection("events").document(postId).update(likeData);


                                                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                            } else {
                                                Log.d("TAG", "No such document");
                                            }
                                        } else {
                                            Log.d("TAG", "get failed with ", task.getException());
                                        }
                                    }
                                });


                                //add the post to the likes and generate notification
                                db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {


                                                Map<String, Object> data = new HashMap<>();
                                                data.put("author", author);
                                                data.put("authorPfp", authorPfp);
                                                data.put("location", location);
                                                data.put("likeVal", likesVal);
                                                data.put("commentVal", commentVal);
                                                data.put("date", date);
                                                data.put("description", description);
                                                data.put("contentUrl", contentUrl);
                                                data.put("postId", postId);

                                                db.collection("users").document(userId).collection("likes").document(postId).set(data);

                                                String name = document.getString("name");
                                                String pfpUrl = document.getString("pfp");
                                                String contentTxt = name + " has liked your picture!";
                                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                                                Date date = new Date();
                                                String dateTxt = formatter.format(date).toString();
                                                String notifId = UUID.randomUUID().toString();

                                                String authorId = contentEvents.getAuthorId();

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


                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });


            }


        });


        //Reward selection logic (try with Switch/case later!)
        holder.btnReward.setOnClickListener(new View.OnClickListener() {
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
                                Context context = view.getContext();
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, message, duration);
                                toast.show();


                            } else {
                                Log.d("TAG", "No such document");

                                holder.modalView.setVisibility(View.VISIBLE);

                            }


                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });


            }
        });

        holder.btnIdea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.btnImpact.setBackgroundResource(R.drawable.impactful_white);
                holder.btnIdea.setBackgroundResource(R.drawable.ecoidea_green);
                String reward = "IDEA";

                holder.btnOk.setOnClickListener(new View.OnClickListener() {
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

                                        holder.ecoIdeaCounter.setText(newIdeaValTxt);
                                        holder.ecoIdeaBadge.setVisibility(View.VISIBLE);
                                        holder.ecoIdeaCounter.setVisibility(View.VISIBLE);

                                        Map<String, Object> rewardData = new HashMap<>();
                                        rewardData.put("ecoIdea", newIdeaValTxt);

                                        db.collection("users").document(authorId).collection("posts").document(postId).update(rewardData);
                                        db.collection("users").document(authorId).update(rewardData);

                                        //add postId to our rewarded posts
                                        Map<String, Object> postRewardData = new HashMap<>();
                                        postRewardData.put("author", author);
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
                                                        String contentTxt = name + " rewarded your post with: Eco Idea";
                                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                                                        Date date = new Date();
                                                        String dateTxt = formatter.format(date).toString();
                                                        String notifId = UUID.randomUUID().toString();

                                                        String authorId = contentEvents.getAuthorId();

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

                        holder.modalView.setVisibility(View.INVISIBLE);
                        Snackbar.make(view, "Award given!", Snackbar.LENGTH_SHORT).show();

                    }
                });
            }
        });

        holder.btnImpact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.btnImpact.setBackgroundResource(R.drawable.impactful_green);
                holder.btnIdea.setBackgroundResource(R.drawable.ecoidea_white);
                String reward = "IMAPCTFUL";

                holder.btnOk.setOnClickListener(new View.OnClickListener() {
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

                                        holder.impactfulCounter.setText(newImpactfulValTxt);
                                        holder.impactfulCounter.setVisibility(View.VISIBLE);
                                        holder.impactfulBadge.setVisibility(View.VISIBLE);

                                        Map<String, Object> rewardData = new HashMap<>();
                                        rewardData.put("impactful", newImpactfulValTxt);

                                        db.collection("users").document(authorId).collection("posts").document(postId).update(rewardData);
                                        db.collection("users").document(authorId).update(rewardData);

                                        //add postId to our rewarded posts
                                        Map<String, Object> postRewardData = new HashMap<>();
                                        postRewardData.put("author", author);
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

                                                        String authorId = contentEvents.getAuthorId();

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

                        holder.modalView.setVisibility(View.INVISIBLE);
                        Snackbar.make(view, "Award given!", Snackbar.LENGTH_SHORT).show();

                    }

                });
            }
        });

        holder.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.modalView.setVisibility(View.INVISIBLE);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

