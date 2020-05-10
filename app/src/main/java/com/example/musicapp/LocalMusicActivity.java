package com.example.musicapp;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.musicapp.adapter.FLBAdapter;
import com.example.musicapp.db.FLBMusic;

import java.util.ArrayList;
import java.util.List;

public class LocalMusicActivity extends AppCompatActivity {

    private FLBMusic[] flbMusics = {
            new FLBMusic("可惜没如果", "林俊杰", "可惜啊"), new FLBMusic("子在西元前", "周杰伦", "爱在西亚"),
            new FLBMusic("Can We Kiss Forever", "JBSD", "WWWW"), new FLBMusic("较简洁", "是", "是"),
            new FLBMusic("12313", "123", "可惜啊123"), new FLBMusic("可惜没如果", "林俊杰", "可惜啊"),
            new FLBMusic("和欢呼声", "林我去俊杰", "我去"), new FLBMusic("大噶", "爱所", "爱所撒"),
            new FLBMusic("给", "大概", "傻瓜"), new FLBMusic("可惜没爱所是如果", "而我却", "我去"),
            new FLBMusic(" 分数", "大水缸", "大水缸"), new FLBMusic("可惜没撒撒如果", "我去", "儿媳妇"),
            new FLBMusic("可惜没而且额提到过如果", "神功大", "可惜啊"), new FLBMusic("撒", "我去", "可惜啊"),
            new FLBMusic("前额头铁", " 而且", "请问他"), new FLBMusic("给都是", "去微软", "额我确认"),};

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
        for (int i = 0; i < flbMusics.length; i++) {
            flbMusicList.add(flbMusics[i]);
        }
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
