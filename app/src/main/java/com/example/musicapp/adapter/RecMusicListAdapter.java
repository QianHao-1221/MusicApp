package com.example.musicapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.RecMusicListInfoActivity;
import com.example.musicapp.db.RecMusicList;

import java.util.List;

public class RecMusicListAdapter extends RecyclerView.Adapter<RecMusicListAdapter.ViewHolder> {

    private Context mContext;

    private List<RecMusicList> mMusicList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView musicListImage;
        TextView musicListName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            musicListImage = (ImageView) view.findViewById(R.id.rec_music_image);
            musicListName = (TextView) view.findViewById(R.id.rec_music_name);
        }
    }

    public RecMusicListAdapter(List<RecMusicList> MusicList) {
        mMusicList = MusicList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.rec_music, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                RecMusicList recMusicList = mMusicList.get(position);
                Intent intent = new Intent(mContext, RecMusicListInfoActivity.class);
                intent.putExtra(RecMusicListInfoActivity.MUSIC_NAME, recMusicList.getName());
                intent.putExtra(RecMusicListInfoActivity.MUSIC_IMAGE_ID, recMusicList.getImageId());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecMusicList musicList = mMusicList.get(position);
        holder.musicListName.setText(musicList.getName());
        Glide.with(mContext).load(musicList.getImageId()).into(holder.musicListImage);
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }
}
