package com.example.musicapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.musicapp.adapter.FOHRLAdapter;
import com.example.musicapp.adapter.SingerRankAdapter;
import com.example.musicapp.db.FLBMusic;
import com.example.musicapp.db.MusicInfo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class RankActivity extends AppCompatActivity {

    private List<FLBMusic> musicRank = new ArrayList<>();

    private List<FLBMusic> singerRank = new ArrayList<>();

    private FOHRLAdapter musicRankAdapter;

    private SingerRankAdapter singerRankAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.singer_rank);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        singerRankAdapter = new SingerRankAdapter(singerRank);
        recyclerView.setAdapter(singerRankAdapter);

        RecyclerView recyclerView1 = (RecyclerView) findViewById(R.id.music_rank);
        GridLayoutManager layoutManager1 = new GridLayoutManager(this, 1);
        recyclerView1.setLayoutManager(layoutManager1);
        musicRankAdapter = new FOHRLAdapter(musicRank, "RankActivity");
        recyclerView1.setAdapter(musicRankAdapter);

        initMusicList();
    }

    private void initMusicList() {
        musicRank.clear();
        singerRank.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Cursor cursor = LitePal.findBySQL("select * from MusicInfo " +
                                "order by music_name limit 10;");
                        if (cursor.moveToFirst()) {
                            do {
                                FLBMusic flbMusic = new FLBMusic(cursor.getString(cursor.getColumnIndex("music_name")),
                                        cursor.getString(cursor.getColumnIndex("music_player")),
                                        cursor.getString(cursor.getColumnIndex("music_package")));
                                musicRank.add(flbMusic);
                            } while (cursor.moveToNext());
                        }
                        Cursor cursor1 = LitePal.findBySQL("select distinct music_player from MusicInfo " +
                                "order by music_player limit 5;");
                        if (cursor1.moveToFirst()) {
                            do {
                                FLBMusic flbMusic = new FLBMusic(cursor1.getString(cursor1.getColumnIndex("music_player")), "", "");
                                singerRank.add(flbMusic);
                            } while (cursor1.moveToNext());
                        }
                    }
                });
            }
        }).start();
    }

    public void getLocal(final FLBMusic flbMusic, final int playFlag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MusicInfo> musicInfos = LitePal.where("music_package = ?", flbMusic.getPageName()).find(MusicInfo.class);
                for (MusicInfo musicInfo : musicInfos) {
                    //向MA中传值
                    Intent intent = new Intent();
                    intent.putExtra("flb_path", musicInfo.getMusic_package());
                    intent.putExtra("returnImg", musicInfo.getImage_no());
                    intent.putExtra("flb_playFlag", playFlag);
                    intent.putExtra("flb_way", 1);
                    setResult(RESULT_OK, intent);
                }
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }
}
