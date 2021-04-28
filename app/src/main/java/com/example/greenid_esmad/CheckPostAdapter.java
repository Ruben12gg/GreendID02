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

import java.util.ArrayList;
import java.util.List;

public class CheckPostAdapter extends RecyclerView.Adapter<CheckPostAdapter.ViewHolder> {

    //criação de referencias
    private List<ContentCheckPost> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    CheckPostAdapter(Context context, ArrayList<ContentCheckPost> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.comments_card, parent, false);
        return new ViewHolder(view);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        ImageView pfp;
        TextView tvAuthor;
        TextView tvCommentVal;

        RelativeLayout resultCard;


        ViewHolder(View itemView) {
            super(itemView);


            pfp = itemView.findViewById(R.id.profile_image);
            tvAuthor = itemView.findViewById(R.id.username);
            tvCommentVal = itemView.findViewById(R.id.comment);
            resultCard = itemView.findViewById(R.id.comments_card);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        ContentCheckPost contentCheckPost = mData.get(position);

        final String pfp = contentCheckPost.getAuthorPfp();
        final String author = contentCheckPost.getAuthor();
        final String comment = contentCheckPost.getCommentVal();

        holder.tvAuthor.setText(author);
        holder.tvCommentVal.setText(comment);

        Picasso.get().load(pfp).into(holder.pfp);


        holder.resultCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("COMMENTCARD", "Clicked on Comment Card ");



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

