package com.example.greenid_esmad;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    //criação de referencias
    private List<ContentFeed> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;


    FeedAdapter(Context context, List<ContentFeed> data) {
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
        ImageButton btnLike;
        RelativeLayout resultCard;




        ViewHolder(View itemView) {
            super(itemView);

            tvAuthor = itemView.findViewById(R.id.likes_val);
            tvLikeVal = itemView.findViewById(R.id.comments_val);
            tvCommentVal = itemView.findViewById(R.id.location);
            tvLocation = itemView.findViewById(R.id.pf_name);
            contentPic = itemView.findViewById(R.id.post_image);
            pfp = itemView.findViewById(R.id.pfp);
            tvDescription = itemView.findViewById(R.id.descriptionText);
            tvDate = itemView.findViewById(R.id.dateText);
            btnLike = itemView.findViewById(R.id.favorites_icon);
            resultCard = itemView.findViewById(R.id.post_card_02);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        ContentFeed contentFeed = mData.get(position);
        holder.tvAuthor.setText(contentFeed.getAuthor());
        holder.tvDate.setText(contentFeed.getDate());
        holder.tvLikeVal.setText(contentFeed.getLikeVal());
        holder.tvCommentVal.setText(contentFeed.getCommentVal());
        holder.tvLocation.setText(contentFeed.getLocation());
        holder.tvDescription.setText(contentFeed.getDescription());


        final String authorPfp = contentFeed.getAuthorPfp();
        final String contentUrl = contentFeed.getContentUrl();


        Picasso.get().load(authorPfp).into(holder.pfp);
        Picasso.get().load(contentUrl).into(holder.contentPic);

        String author = contentFeed.getAuthor();
        String location = contentFeed.getLocation();
        String likesVal = contentFeed.getLikeVal();
        String commentVal = contentFeed.getCommentVal();
        String date = contentFeed.getDate();
        String description = contentFeed.getDescription();
        String postId = contentFeed.getPostId();
        String userId = contentFeed.getUserId();

        Log.d("POSTID", postId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Change followBtn text accordingly if the user follows the person or not
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


        holder.resultCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("POSTCARD", "Clicked on Post Card");

                Intent i = new Intent(v.getContext(), CheckPost.class);
                i.putExtra("author", author);
                i.putExtra("location", location);
                i.putExtra("likeVal", likesVal);
                i.putExtra("commentVal", commentVal);
                i.putExtra("date", date);
                i.putExtra("description", description);
                i.putExtra("contentUrl", contentUrl);
                i.putExtra("postId", postId);
                v.getContext().startActivity(i);


            }
        });

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("BTNLIKE", "Click on like Btnnn");

                String newLikesVal = author + 1;
                holder.tvLikeVal.setText(newLikesVal);



                Log.d("USERID", userId);
                Log.d("POSTID", postId);
                Log.d("LIKEVAL", author);

                db.collection("users").document(userId).collection("likes").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                holder.btnLike.setImageResource(R.drawable.leaf);
                                db.collection("users").document(userId).collection("likes").document(postId).delete();


                            } else {

                                holder.btnLike.setImageResource(R.drawable.leaf_green);

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

                                                String authorId = contentFeed.getAuthorId();

                                                Map<String, Object> dataNotif = new HashMap<>();
                                                dataNotif.put("username", name);
                                                dataNotif.put("pfpUrl", pfpUrl);
                                                dataNotif.put("contentUrl", contentUrl);
                                                dataNotif.put("commentVal", contentTxt);
                                                dataNotif.put("date", dateTxt);
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

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

