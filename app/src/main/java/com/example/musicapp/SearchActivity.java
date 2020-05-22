package com.example.musicapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.musicapp.adapter.FOHRLAdapter;
import com.example.musicapp.db.FLBMusic;
import com.example.musicapp.db.MusicInfo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private List<FLBMusic> flbMusicList = new ArrayList<>();

    private SearchView searchView;

    private FOHRLAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_recycle_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FOHRLAdapter(flbMusicList, "SearchActivity");
        recyclerView.setAdapter(adapter);

        //搜索列表获取
        searchView = (SearchView) findViewById(R.id.search_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            searchView.findViewById(android.support.v7.appcompat.R.id.search_plate).setBackground(null);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            searchView.findViewById(android.support.v7.appcompat.R.id.submit_area).setBackground(null);
        }

        //搜索列表点击事件
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flbMusicList.clear();
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            //搜索栏提交事件
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            //搜索栏改变事件
            @Override
            public boolean onQueryTextChange(String newText) {
                flbMusicList.clear();
                return false;
            }
        });

        //搜索栏关闭事件
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
    }


    private void search(final String text) {
        flbMusicList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<MusicInfo> lists = LitePal.where("music_name like ?", "%" + text + "%").order("music_name").find(MusicInfo.class);
                        for (MusicInfo musicInfo : lists) {
                            FLBMusic flbMusic = new FLBMusic(musicInfo.getMusic_name(), musicInfo.getMusic_player(), musicInfo.getMusic_package());
                            flbMusicList.add(flbMusic);
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
