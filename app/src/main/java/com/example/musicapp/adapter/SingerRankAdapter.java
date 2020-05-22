package com.example.musicapp.adapter;

import android.content.Context;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.R;
import com.example.musicapp.db.FLBMusic;
import com.example.musicapp.db.MyFav;

import org.litepal.LitePal;

import java.util.List;

public class SingerRankAdapter extends RecyclerView.Adapter<SingerRankAdapter.ViewHolder> {

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

    public SingerRankAdapter(List<FLBMusic> flbMusics) {
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
            }
        });
        return holder;
    }

    public void removeFromFav(int position, final String userNo) {
        flbMusic = mFLBmusic.get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                List<MyFav> myFavList = LitePal.where("user_no = ? and music_name = ? and singer_name = ?", userNo, flbMusic.getMusicName(), flbMusic.getSingerName()).find(MyFav.class);
                if (myFavList.size() != 0) {
                    LitePal.deleteAll(MyFav.class, "user_no = ? and music_name = ? and singer_name = ?", userNo, flbMusic.getMusicName(), flbMusic.getSingerName());
                    Toast.makeText(mContext, "已经移出我喜欢", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "已经移出我喜欢", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        }).start();
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
