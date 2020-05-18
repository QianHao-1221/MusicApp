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

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ViewHolder> {

    private Context mContext;

    private List<FLBMusic> mFLBmusic;

    private OnLongClickListener mLongClickListener;

    private FLBMusic flbMusic;

//    private DownloadService.DownloadBinder downloadBinder;

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

    public RecAdapter(List<FLBMusic> flbMusics) {
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
                //开始下载
//                String url = "https://www.iconfont.cn/api/icon/downloadIcon?type=png&ids=1312167|-1&path_attributes=fill%3D%22%23769aff%22%7Cfill%3D%22%23769aff%22%7Cfill%3D%22%23ffffff%22&size=80&ctoken=Sb6toef0INzBii2HsW8mEw_u";
//                downloadBinder.startDownload(url);
            }
        });
        return holder;
    }

    public void addToFav(int position, final String userNo) {
        flbMusic = mFLBmusic.get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                List<MyFav> myFavList = LitePal.where("user_no = ? and music_name = ?", userNo, flbMusic.getMusicName()).find(MyFav.class);
                if (myFavList.size() == 0) {
                    MyFav m = new MyFav();
                    m.setUser_no(userNo);
                    m.setMusic_name(flbMusic.getMusicName());
                    m.setSinger_name(flbMusic.getSingerName());
                    m.setPage_name(flbMusic.getPageName());
                    m.save();
                    Toast.makeText(mContext, "成功添加到我喜欢！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "已经添加过啦", Toast.LENGTH_SHORT).show();
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
