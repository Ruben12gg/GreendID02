package com.example.greenid_esmad;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    //criação de referencias
    private List<ContentUser> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    UserAdapter(Context context, ArrayList<ContentUser> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_data, parent, false);
        return new ViewHolder(view);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {



        ImageView contentPic;
        RelativeLayout resultCard;


        ViewHolder(View itemView) {
            super(itemView);


            contentPic = itemView.findViewById(R.id.imgGV);
            resultCard = itemView.findViewById(R.id.pfImgs);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        ContentUser contentUser = mData.get(position);

        final String contentUrl = contentUser.getContentUrl();
        final String pfpUrl = contentUser.getAuthorPfp();

        Picasso.get().load(pfpUrl).into(holder.contentPic);

        String author = contentUser.getAuthor();
        String location = contentUser.getLocation();
        String likesVal = contentUser.getLikeVal();
        String commentVal = contentUser.getCommentVal();
        String date = contentUser.getDate();
        String description = contentUser.getDescription();
        String postId = contentUser.getPostId();
        String userId = contentUser.getUserId();



        holder.resultCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(v.getContext(), CheckPost.class);
                i.putExtra("author", author);
                i.putExtra("authorId", userId);
                i.putExtra("location", location);
                i.putExtra("likeVal", likesVal);
                i.putExtra("commentVal", commentVal);
                i.putExtra("date", date);
                i.putExtra("description", description);
                i.putExtra("contentUrl", contentUrl);
                i.putExtra("postId", postId);
                i.putExtra("authorPfp", author);
                v.getContext().startActivity(i);


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

