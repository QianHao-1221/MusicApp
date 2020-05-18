package com.example.musicapp;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.musicapp.adapter.FLBAdapter;
import com.example.musicapp.db.FLBMusic;
import com.example.musicapp.db.MyFav;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class FavActivity extends AppCompatActivity {

    private List<FLBMusic> flbMusicList = new ArrayList<>();

    private FLBAdapter adapter;

    private String userNo;

    private int userSituation;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        initMusicList();

        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        userNo = preferences.getString("userNo", "");
        userSituation = preferences.getInt("userSituation", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.fav_recycle_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FLBAdapter(flbMusicList);
        recyclerView.setAdapter(adapter);

        final SearchView searchView = (SearchView) findViewById(R.id.fav_search_view);
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

        adapter.setLongClickListener(new FLBAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(int position) {
                adapter.removeFromFav(position, userNo);
                return true;
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_buy);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRexMusicList();
            }
        });
    }

    private void refreshRexMusicList() {
        //下拉刷新操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initMusicList();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void initMusicList() {
        flbMusicList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<MyFav> lists = LitePal.order("music_name").find(MyFav.class);
                        for (MyFav myFav : lists) {
                            FLBMusic flbMusic = new FLBMusic(myFav.getMusic_name(), myFav.getSinger_name(), myFav.getPage_name());
                            flbMusicList.add(flbMusic);
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
