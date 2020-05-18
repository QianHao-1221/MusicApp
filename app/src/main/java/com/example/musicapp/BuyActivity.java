package com.example.musicapp;

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
import com.example.musicapp.db.History;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class BuyActivity extends AppCompatActivity {

    private List<FLBMusic> flbMusicList = new ArrayList<>();

    private FLBAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initMusicList();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.buy_recycle_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FLBAdapter(flbMusicList);
        recyclerView.setAdapter(adapter);

        final SearchView searchView = (SearchView) findViewById(R.id.buy_search_view);
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

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_buy);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRexMusicList();
            }
        });

        adapter.setLongClickListener(new FLBAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(int position) {
                return false;
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
                        List<History> histories = LitePal.order("time desc").find(History.class);//查history表根据时间逆序输出数据
                        for (History history : histories) {
                            FLBMusic flbMusic = new FLBMusic(history.getMusic_name(), history.getSinger_name(), history.getPath());
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
