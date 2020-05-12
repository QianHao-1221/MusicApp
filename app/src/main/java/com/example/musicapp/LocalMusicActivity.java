package com.example.musicapp;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.musicapp.adapter.FLBAdapter;
import com.example.musicapp.db.FLBMusic;
import com.example.musicapp.db.Music;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class LocalMusicActivity extends AppCompatActivity {

    private List<FLBMusic> flbMusicList = new ArrayList<>();

    private FLBAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);

        initMusicList();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.local_recycle_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FLBAdapter(flbMusicList);
        recyclerView.setAdapter(adapter);

        final SearchView searchView = (SearchView) findViewById(R.id.local_search_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            searchView.findViewById(android.support.v7.appcompat.R.id.search_plate).setBackground(null);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            searchView.findViewById(android.support.v7.appcompat.R.id.submit_area).setBackground(null);
        }

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
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
                        List<Music> musics = LitePal.order("music_name").find(Music.class);
                        for (Music music : musics) {
                            Log.e("LocalMusicActivity", "" + music.getMusic_name());
                            FLBMusic m = new FLBMusic(music.getMusic_name(), music.getMusic_player(), music.getMusic_package());
                            flbMusicList.add(m);
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
                break;
            default:
        }
        return true;
    }
}
