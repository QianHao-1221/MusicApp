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
import com.example.musicapp.UserMusicListActivity;
import com.example.musicapp.db.RecMusicList;

import java.util.List;

public class UserMusicListAdapter extends RecyclerView.Adapter<UserMusicListAdapter.ViewHolder> {

    private Context mContext;

    private List<RecMusicList> mMusicList;

    private OnLongClickListener mLongClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView musicListImage;
        TextView musicListName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            musicListImage = (ImageView) view.findViewById(R.id.user_music_list_pic);
            musicListName = (TextView) view.findViewById(R.id.user_music_list_name);
        }
    }

    public UserMusicListAdapter(List<RecMusicList> recMusicLists) {
        mMusicList = recMusicLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_music_list_layout, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                RecMusicList recMusicList = mMusicList.get(position);
                Intent intent = new Intent(mContext, UserMusicListActivity.class);
                intent.putExtra(RecMusicListInfoActivity.MUSIC_NAME, recMusicList.getName());
                intent.putExtra(RecMusicListInfoActivity.MUSIC_IMAGE_ID, recMusicList.getImageId());
                mContext.startActivity(intent);
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

    public void addData(int position, String name, int imageId) {
        RecMusicList recMusicList = new RecMusicList(name, imageId);
        mMusicList.add(recMusicList);
        notifyItemInserted(position);
    }


    public void removeData(int position) {
        mMusicList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return mLongClickListener.onLongClick(position);
            }
        });
        RecMusicList recMusicList = mMusicList.get(position);
        holder.musicListName.setText(recMusicList.getName());
        Glide.with(mContext).load(recMusicList.getImageId()).into(holder.musicListImage);
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }
}
