package com.example.greenid_esmad;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    //criação de referencias
    private List<ContentNotifications> mData;
    private LayoutInflater mInflater;
    private NotificationsAdapter.ItemClickListener mClickListener;

    NotificationsAdapter(Context context, List<ContentNotifications> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.notification_card, parent, false);
        return new NotificationsAdapter.ViewHolder(view);


    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvAuthor;
        TextView tvCommentVal;
        ImageView profile_image;
        ImageView contentPic;
        ImageButton dots;
        ImageButton delNotif;
        ImageButton close;
        RelativeLayout delView;
        RelativeLayout resultCard;


        ViewHolder(View itemView) {
            super(itemView);

            tvAuthor = itemView.findViewById(R.id.username);
            tvCommentVal = itemView.findViewById(R.id.comment);
            contentPic = itemView.findViewById(R.id.contentPic);
            profile_image = itemView.findViewById(R.id.profile_image);
            dots = itemView.findViewById(R.id.dots);
            delNotif = itemView.findViewById(R.id.delNotif);
            close = itemView.findViewById(R.id.closeDel);
            delView = itemView.findViewById(R.id.delView);
            resultCard = itemView.findViewById(R.id.notifications_card);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        ContentNotifications contentNotifications = mData.get(position);
        holder.tvAuthor.setText(contentNotifications.getAuthor());
        holder.tvCommentVal.setText(contentNotifications.getCommentVal());


        final String authorPfp = contentNotifications.getAuthorPfp();
        final String contentUrl = contentNotifications.getContentUrl();

        Picasso.get().load(authorPfp).into(holder.profile_image);

        final String notifId = contentNotifications.getNotifId();
        final String userId = contentNotifications.getUserId();
        final String authorId = contentNotifications.getAuthorId();

        holder.delView.setVisibility(View.GONE);


        holder.resultCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (contentUrl.isEmpty() || contentUrl == null) {

                    Intent i = new Intent(v.getContext(), CheckUser.class);
                    i.putExtra("bio", authorId);
                    v.getContext().startActivity(i);

                } else {

                    //Get profile Data
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(userId).collection("notifications").document(notifId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {

                                    String postId = document.getString("postId");
                                    Intent i = new Intent(v.getContext(), CheckPost.class);
                                    i.putExtra("authorId", userId);
                                    i.putExtra("postId", postId);
                                    v.getContext().startActivity(i);

                                    Log.d("authorID + postID", userId + " " + postId);

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
        });

        holder.resultCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Log.d("LONG CLICK", "Long click on the notification: ");

                return false;
            }
        });

        holder.dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.delView.setVisibility(View.VISIBLE);
            }
        });

        holder.delNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(userId).collection("notifications").document(notifId).delete();
                holder.delView.setVisibility(View.GONE);
            }
        });

        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.delView.setVisibility(View.GONE);
            }
        });

        //Check if there's an image to show on the notification to prevent crashing from trying to load null img src into imgView
        if (contentUrl.isEmpty() || contentUrl == null) {
            return;
        } else {
            Picasso.get().load(contentUrl).into(holder.contentPic);
        }


    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


}

