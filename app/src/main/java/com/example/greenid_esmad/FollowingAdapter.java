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

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder> {

    //criação de referencias
    private List<ContentFollowing> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    FollowingAdapter(Context context, ArrayList<ContentFollowing> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.profile_card, parent, false);
        return new ViewHolder(view);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvProfileName;
        ImageView pfp;
        RelativeLayout resultCard;


        ViewHolder(View itemView) {
            super(itemView);

            tvProfileName = itemView.findViewById(R.id.profileName);
            pfp = itemView.findViewById(R.id.pfpSearch);
            resultCard = itemView.findViewById(R.id.pf_card_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        ContentFollowing contentFollowing = mData.get(position);

        holder.tvProfileName.setText(contentFollowing.getProfileName());

        final String pfpPic = contentFollowing.getPfp();
        Picasso.get().load(pfpPic).into(holder.pfp);

        final String pfName = contentFollowing.getProfileName();
        final String id = contentFollowing.getId();


        holder.resultCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(v.getContext(), CheckUser.class);
                i.putExtra("pfName", pfName);
                i.putExtra("pfp", pfpPic);
                i.putExtra("bio", id);
                Log.d("ID", id);
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

