package com.example.greenid_esmad;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    //criação de referencias
    private List<ContentFeed> mData; //class dos AnimeLiked
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    FeedAdapter(Context context, List<ContentFeed> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.post_card, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        ContentFeed contentFeed = mData.get(position);
        holder.tvAuthor.setText(contentFeed.getAuthor());
        /*holder.tvdate.setText(contentFeed.getDate());*/
        holder.tvLikeVal.setText(contentFeed.getLikeVal());
        holder.tvCommentVal.setText(contentFeed.getCommentVal());


        final String authorPfp = ContentFeed.getAuthorPfp();
        final String contentUrl = ContentFeed.getContentUrl();


        Picasso.get().load(authorPfp).into(holder.pfp);
        Picasso.get().load(contentUrl).into(holder.img);


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
        TextView tvdate;
        TextView tvLikeVal;
        TextView tvCommentVal;
        ImageView pfp;
        ImageView contentPic;
        ImageView img;
        RelativeLayout resultCard;


        ViewHolder(View itemView) {
            super(itemView);

            tvAuthor = itemView.findViewById(R.id.pf_name);
            tvLikeVal = itemView.findViewById(R.id.likes_val);
            tvCommentVal = itemView.findViewById(R.id.comments_val);
            contentPic = itemView.findViewById(R.id.post_image);
            img = itemView.findViewById(R.id.img);
            pfp = itemView.findViewById(R.id.pfp);
            resultCard = itemView.findViewById(R.id.post_card);
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

