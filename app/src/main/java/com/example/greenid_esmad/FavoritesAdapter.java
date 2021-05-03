package com.example.greenid_esmad;

import android.content.Context;
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

import com.squareup.picasso.Picasso;

import java.util.List;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    //criação de referencias
    private List<ContentFavorites> mData;
    private LayoutInflater mInflater;
    private FavoritesAdapter.ItemClickListener mClickListener;

    FavoritesAdapter(Context context, List<ContentFavorites> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.favorites_card, parent, false);
        return new FavoritesAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.ViewHolder holder, int position) {


        ContentFavorites contentFavorites = mData.get(position);
        holder.tvAuthor.setText(contentFavorites.getAuthor());
        holder.tvDate.setText(contentFavorites.getDate());
        holder.tvLikeVal.setText(contentFavorites.getLikeVal());
        holder.tvCommentVal.setText(contentFavorites.getCommentVal());
        holder.tvLocation.setText(contentFavorites.getLocation());
        holder.tvDescription.setText(contentFavorites.getDescription());

        Log.d("AUTHOR", contentFavorites.getAuthor());
        Log.d("DATE", contentFavorites.getDate());
        Log.d("LIKES", contentFavorites.getLikeVal());
        Log.d("COMMENTS", contentFavorites.getCommentVal());
        Log.d("LOCATION", contentFavorites.getLocation());
        Log.d("DESCRIPTION", contentFavorites.getDescription());


        final String authorPfp = contentFavorites.getAuthorPfp();
        final String contentUrl = contentFavorites.getContentUrl();


        Picasso.get().load(authorPfp).into(holder.pfp);
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
        TextView tvDate;
        TextView tvLikeVal;
        TextView tvLocation;
        TextView tvCommentVal;
        TextView tvDescription;
        ImageView pfp;
        ImageView contentPic;
        ImageButton btnLike;
        ImageButton btnComment;
        ImageButton btnSaved;
        RelativeLayout resultCard;


        ViewHolder(View itemView) {
            super(itemView);

            tvAuthor = itemView.findViewById(R.id.comments_val);
            tvLikeVal = itemView.findViewById(R.id.location);
            tvCommentVal = itemView.findViewById(R.id.pf_name);
            tvLocation = itemView.findViewById(R.id.likes_val);
            contentPic = itemView.findViewById(R.id.post_image);
            pfp = itemView.findViewById(R.id.pfp);
            tvDescription = itemView.findViewById(R.id.descriptionText);
            tvDate = itemView.findViewById(R.id.dateText);
            btnLike = itemView.findViewById(R.id.favorites_icon);
            btnComment = itemView.findViewById(R.id.comments_icon);
            btnSaved = itemView.findViewById(R.id.btnSaved);
            resultCard = itemView.findViewById(R.id.favorite_card);
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
