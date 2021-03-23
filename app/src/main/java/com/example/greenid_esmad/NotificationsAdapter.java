package com.example.greenid_esmad;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        ContentNotifications contentNotifications = mData.get(position);
        holder.tvAuthor.setText(contentNotifications.getAuthor());
        holder.tvCommentVal.setText(contentNotifications.getCommentVal());


        final String authorPfp = contentNotifications.getAuthorPfp();
        final String contentUrl = contentNotifications.getContentUrl();


        Picasso.get().load(authorPfp).into(holder.profile_image);
        Picasso.get().load(contentUrl).into(holder.contentPic);


        holder.resultCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "XPTO");


            }
        });

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvAuthor;
        TextView tvCommentVal;
        ImageView profile_image;
        ImageView contentPic;
        RelativeLayout resultCard;


        ViewHolder(View itemView) {
            super(itemView);

            tvAuthor = itemView.findViewById(R.id.username);
            tvCommentVal = itemView.findViewById(R.id.comment);
            contentPic = itemView.findViewById(R.id.contentPic);
            profile_image = itemView.findViewById(R.id.profile_image);
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
}