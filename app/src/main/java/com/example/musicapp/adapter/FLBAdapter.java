package com.example.musicapp.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musicapp.R;
import com.example.musicapp.db.FLBMusic;

import java.util.List;

public class FLBAdapter extends RecyclerView.Adapter<FLBAdapter.ViewHolder> {

    private Context mContext;

    private List<FLBMusic> mFLBmusic;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView musicName;
        TextView singerName;
        TextView pageName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            musicName = (TextView) view.findViewById(R.id.music_name);
            singerName = (TextView) view.findViewById(R.id.singer_name);
            pageName = (TextView) view.findViewById(R.id.page_name);
        }
    }

    public FLBAdapter(List<FLBMusic> flbMusics) {
        mFLBmusic = flbMusics;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flb_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FLBMusic flbMusic = mFLBmusic.get(position);
        holder.musicName.setText(flbMusic.getMusicName());
        holder.singerName.setText(flbMusic.getSingerName());
        holder.pageName.setText(flbMusic.getPageName());
    }

    @Override
    public int getItemCount() {
        return mFLBmusic.size();
    }
}
