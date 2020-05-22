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

import com.example.musicapp.FavActivity;
import com.example.musicapp.HistoryActivity;
import com.example.musicapp.LocalMusicActivity;
import com.example.musicapp.R;
import com.example.musicapp.RankActivity;
import com.example.musicapp.SearchActivity;
import com.example.musicapp.db.FLBMusic;
import com.example.musicapp.db.MyFav;
import com.example.musicapp.db.MyMusicListInfo;
import com.example.musicapp.service.MusicService;

import org.litepal.LitePal;

import java.util.List;

public class FOHRLAdapter extends RecyclerView.Adapter<FOHRLAdapter.ViewHolder> {

    private Context mContext;

    private List<FLBMusic> mFLBmusic;

    private OnLongClickListener mLongClickListener;

    private FLBMusic flbMusic;

    private String flag;

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

    public FOHRLAdapter(List<FLBMusic> flbMusics, String flag) {
        mFLBmusic = flbMusics;
        this.flag = flag;
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
                switch (flag) {
                    case "FavActivity":
                        FavActivity favActivity = (FavActivity) view.getContext();
                        favActivity.getLocal(flbMusic, 1);
                        break;
                    case "HistoryActivity":
                        HistoryActivity historyActivity = (HistoryActivity) view.getContext();
                        historyActivity.getLocal(flbMusic, 1);
                        break;
                    case "LocalMusicActivity":
                        LocalMusicActivity localMusicActivity = (LocalMusicActivity) view.getContext();
                        localMusicActivity.getLocal(flbMusic, 1);
                        break;
                    case "RankActivity":
                        RankActivity rankActivity = (RankActivity) view.getContext();
                        rankActivity.getLocal(flbMusic, 1);
                        break;
                    case "SearchActivity":
                        SearchActivity searchActivity = (SearchActivity) view.getContext();
                        searchActivity.getLocal(flbMusic, 1);
                        break;
                    case "UserMusicListInfoActivity":

                        break;
                    default:
                }
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

    public void removeFromList(final int position, final String userNo, final String name) {
        flbMusic = mFLBmusic.get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                List<MyMusicListInfo> myMusicListInfos = LitePal.where("user_no = ? and list_name = ? and music_name = ? and singer_name = ?", userNo, name, flbMusic.getMusicName(), flbMusic.getSingerName()).find(MyMusicListInfo.class);
                if (myMusicListInfos.size() != 0) {
                    LitePal.deleteAll(MyMusicListInfo.class, "user_no = ? and list_name = ? and music_name = ? and singer_name = ?", userNo, name, flbMusic.getMusicName(), flbMusic.getSingerName());
                    Toast.makeText(mContext, "已经移出歌单", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "已经移出歌单", Toast.LENGTH_SHORT).show();
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
