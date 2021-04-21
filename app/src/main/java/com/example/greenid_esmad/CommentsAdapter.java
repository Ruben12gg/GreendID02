package com.example.greenid_esmad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter {
    //criação de referencias
    private List<ContentComments> mData;
    private LayoutInflater mInflater;
    private CommentsAdapter.ItemClickListener mClickListener;

    CommentsAdapter(Context context, List<ContentComments> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.comments_card, parent, false);
        return new CommentsAdapter.ViewHolder(view);



    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvAuthor;
        TextView tvCommentVal;
        ImageView profile_image;
        RelativeLayout resultCard;



        ViewHolder(View itemView) {
            super(itemView);

            tvAuthor = itemView.findViewById(R.id.username);
            tvCommentVal = itemView.findViewById(R.id.comment);
            profile_image = itemView.findViewById(R.id.profile_image);
            resultCard = itemView.findViewById(R.id.comments_card);
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