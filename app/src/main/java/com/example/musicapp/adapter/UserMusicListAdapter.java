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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.UserMusicListActivity;
import com.example.musicapp.db.MyMusicList;
import com.example.musicapp.db.RecMusicList;

import org.litepal.LitePal;

import java.util.List;

public class UserMusicListAdapter extends RecyclerView.Adapter<UserMusicListAdapter.ViewHolder> {

    private Context mContext;

    private List<RecMusicList> mMusicList;

    private OnLongClickListener mLongClickListener;

    private RecMusicList recMusicList;

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
                recMusicList = mMusicList.get(position);
                Intent intent = new Intent(mContext, UserMusicListActivity.class);
                intent.putExtra(UserMusicListActivity.MUSIC_NAME, recMusicList.getName());
                intent.putExtra(UserMusicListActivity.MUSIC_IMAGE_ID, recMusicList.getImageId());
                mContext.startActivity(intent);
            }
        });
        holder.musicListImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "你好呀", Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    //接口回调
    public void setLongClickListener(OnLongClickListener longClickListener) {
        mLongClickListener = longClickListener;
    }

    //接口
    public interface OnLongClickListener {
        boolean onLongClick(int position);
    }

    public void addData(int position, String name, int imageId) {
        //添加歌单到主页面
        RecMusicList recMusicList = new RecMusicList(name, imageId);
        mMusicList.add(recMusicList);
        notifyItemInserted(position);
    }

    public void removeData(int position, final String userNo) {
        //长按操作
        recMusicList = mMusicList.get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                LitePal.deleteAll(MyMusicList.class, "user_no = ? and list_name = ?", userNo, recMusicList.getName());
            }
        }).start();
        mMusicList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int position = holder.getAdapterPosition();
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
