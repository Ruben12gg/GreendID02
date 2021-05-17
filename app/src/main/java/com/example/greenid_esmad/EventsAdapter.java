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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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



        ContentEvents contentEvents = mData.get(position);
        holder.tvAuthor.setText(contentEvents.getAuthor());
        holder.tvDate.setText(contentEvents.getDate());
        holder.tvLikeVal.setText(contentEvents.getLikeVal());
        holder.tvCommentVal.setText(contentEvents.getCommentVal());
        holder.tvLocation.setText(contentEvents.getLocation());
        holder.tvDescription.setText(contentEvents.getDescription());


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
        String description = contentEvents.getDescription();
        String postId = contentEvents.getPostId();
        String commentId = contentEvents.getCommentId();
        String userId = contentEvents.getUserId();

        if (commentVal.isEmpty()){
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

                /*Intent i = new Intent(v.getContext(), CheckUser.class);
                i.putExtra("pfName", pfName);
                i.putExtra("pfp", pfpPic);
                i.putExtra("bio", bio);
                i.putExtra("followers", followers);
                i.putExtra("following", following);
                i.putExtra("id", id);
                v.getContext().startActivity(i);*/


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

