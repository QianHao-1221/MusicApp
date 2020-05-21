package com.example.musicapp;

import android.content.Intent;
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

import com.example.musicapp.adapter.HistoryAdapter;
import com.example.musicapp.db.FLBMusic;
import com.example.musicapp.db.History;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private List<FLBMusic> flbMusicList = new ArrayList<>();

    private HistoryAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

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
        adapter = new HistoryAdapter(flbMusicList);
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
                flbMusicList.clear();
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                flbMusicList.clear();
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                initMusicList();
                return false;
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

        adapter.setLongClickListener(new HistoryAdapter.OnLongClickListener() {
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

    private void search(final String text) {
        flbMusicList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<History> lists = LitePal.where("music_name like ?", "%" + text + "%").order("music_name").find(History.class);
                        for (History history : lists) {
                            FLBMusic flbMusic = new FLBMusic(history.getMusic_name(), history.getSinger_name(), history.getPath());
                            flbMusicList.add(flbMusic);
                        }
                    }
                });
            }
        }).start();
    }

    public void getLocal(FLBMusic flbMusic, int playFlag) {
        //向MA中传值
        Intent intent = new Intent();
        intent.putExtra("flb_path", flbMusic.getPageName());
        intent.putExtra("flb_playFlag", playFlag);
        intent.putExtra("flb_way", 1);
        setResult(RESULT_OK, intent);
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
