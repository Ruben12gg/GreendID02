package com.example.greenid_esmad;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CheckUserAdapter extends RecyclerView.Adapter<CheckUserAdapter.ViewHolder> {

    //criação de referencias
    private List<ContentCheckUser> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    CheckUserAdapter(Context context, ArrayList<ContentCheckUser> data) {
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


        ContentCheckUser contentCheckUser = mData.get(position);

        final String contentUrl = contentCheckUser.getContentUrl();
        final String authorId = contentCheckUser.getUserId();
        final String postId = contentCheckUser.getPostId();

        Picasso.get().load(contentUrl).into(holder.contentPic);

        /*Log.d("AUTHOR", author);
        Log.d("LOCATION", location);
        Log.d("LIKES", likesVal);
        Log.d("COMMENTS", commentVal);
        Log.d("DATE", date);
        Log.d("DESCRIPTION", description);
        Log.d("POSTID", postId);
        Log.d("CONTENT", contentUrl);
        Log.d("PFP", pfpUrl);*/



        holder.resultCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), CheckPost.class);
                i.putExtra("authorId", authorId);
                i.putExtra("postId", postId);
                i.putExtra("contentUrl", contentUrl);
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

