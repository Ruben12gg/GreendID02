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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    //criação de referencias
    private List<ContentSearch> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    SearchAdapter(Context context, List<ContentSearch> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.search_profile_card, parent, false);
        return new ViewHolder(view);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvProfileName;
        ImageView pfp;
        RelativeLayout resultCard;


        ViewHolder(View itemView) {
            super(itemView);

            tvProfileName = itemView.findViewById(R.id.profileName);
            pfp = itemView.findViewById(R.id.pfp);
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


        ContentSearch contentSearch = mData.get(position);

        holder.tvProfileName.setText(contentSearch.getProfileName());

        final String pfpPic = contentSearch.getPfp();
        Picasso.get().load(pfpPic).into(holder.pfp);


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


    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

