package com.example.musicapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.musicapp.adapter.UserListAdapter;
import com.example.musicapp.db.FLBMusic;
import com.example.musicapp.db.MyMusicListInfo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class UserMusicListInfoActivity extends AppCompatActivity {

    public static final String MUSIC_NAME = "music_name";

    public static final String MUSIC_IMAGE_ID = "music_image_id";

    private List<FLBMusic> flbMusicList = new ArrayList<>();

    private UserListAdapter adapter;

    private String listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_music_list);

        //接受传来的数据：音乐名、图片id
        Intent intent = getIntent();
        String musicName = intent.getStringExtra(MUSIC_NAME);
        int musicImageId = intent.getIntExtra(MUSIC_IMAGE_ID, 0);
        listName = intent.getStringExtra("list_name");

        initMusicList();

        //获取CollapsingToolbarLayout实例
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.user_list_collapsing_toolbar);

        ImageView musicImageView = (ImageView) findViewById(R.id.user_list_image);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.user_list_recycle_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new UserListAdapter(flbMusicList);
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.user_list_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(musicName);
        Glide.with(this).load(musicImageId).into(musicImageView);

        adapter.setLongClickListener(new UserListAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(int position) {
                SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
                adapter.removeFromList(position, preferences.getString("userNo", ""),listName);
                return true;
            }
        });
    }

    private void initMusicList() {
        flbMusicList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
                        List<MyMusicListInfo> myMusicListInfos = LitePal.where("user_no = ? and list_name = ?", preferences.getString("userNo", ""), listName).order("music_name").find(MyMusicListInfo.class);
                        for (MyMusicListInfo myMusicListInfo : myMusicListInfos) {
                            if (myMusicListInfos.size() == 0) {
                                FLBMusic flbMusic = new FLBMusic("", "", "");
                                flbMusicList.add(flbMusic);
                            } else {
                                FLBMusic flbMusic = new FLBMusic(myMusicListInfo.getMusic_name(), myMusicListInfo.getSinger_name(), myMusicListInfo.getPath());
                                flbMusicList.add(flbMusic);
                            }
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
