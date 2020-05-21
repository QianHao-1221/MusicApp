package com.example.musicapp.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musicapp.HistoryActivity;
import com.example.musicapp.R;
import com.example.musicapp.db.FLBMusic;
import com.example.musicapp.service.MusicService;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context mContext;

    private List<FLBMusic> mFLBmusic;

    private OnLongClickListener mLongClickListener;

    private FLBMusic flbMusic;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView musicName;
        TextView singerName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            musicName = (TextView) view.findViewById(R.id.music_name);
            singerName = (TextView) view.findViewById(R.id.singer_name);
        }
    }

    public HistoryAdapter(List<FLBMusic> flbMusics) {
        mFLBmusic = flbMusics;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flb_music, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                flbMusic = mFLBmusic.get(position);
                MusicService musicService = new MusicService();
                musicService.play(flbMusic.getPageName());//点击音乐播放

                //调用HA中的方法，把flbmusic对象传过去
                HistoryActivity historyActivity = (HistoryActivity) view.getContext();
                historyActivity.getLocal(flbMusic, 1);
            }
        });
        return holder;
    }

    public void setLongClickListener(OnLongClickListener longClickListener) {
        mLongClickListener = longClickListener;
    }

    public interface OnLongClickListener {
        boolean onLongClick(int position);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int position = holder.getAdapterPosition();
                return mLongClickListener.onLongClick(position);
            }
        });
        FLBMusic flbMusic = mFLBmusic.get(position);
        holder.musicName.setText(flbMusic.getMusicName());
        holder.singerName.setText(flbMusic.getSingerName());
    }

    @Override
    public int getItemCount() {
        return mFLBmusic.size();
    }
}
