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
        View view = mInflater.inflate(R.layout.notification_card, parent, false);
        return new FavoritesAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.ViewHolder holder, int position) {


        ContentFavorites contentFavorites = mData.get(position);
        holder.tvAuthor.setText(contentFavorites.getAuthor());
        holder.tvCommentVal.setText(contentFavorites.getCommentVal());


        final String authorPfp = contentFavorites.getAuthorPfp();
        final String contentUrl = contentFavorites.getContentUrl();


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
