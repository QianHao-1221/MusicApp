package com.example.musicapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.musicapp.adapter.LocalAdapter;
import com.example.musicapp.db.FLBMusic;
import com.example.musicapp.db.MusicInfo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class LocalMusicActivity extends AppCompatActivity {

    private List<FLBMusic> flbMusicList = new ArrayList<>();

    private LocalAdapter adapter;

    private String musicName, artist, packages;

    private SwipeRefreshLayout swipeRefreshLayout;

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
        adapter = new LocalAdapter(flbMusicList);
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
                flbMusicList.clear();
                searchView.setIconified(false);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                initMusicList();
                return false;
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

        adapter.setLongClickListener(new LocalAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(int position) {
                return false;
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_local);
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
                        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);// 默认排序顺序
                        // 如果游标读取时还有下一个数据，读取
                        int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);//获取列名对应的索引
                        int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);// 标题
                        int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);// 艺术家
                        int uriIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);// 文件路径

                        while (cursor.moveToNext()) {
                            // 根据索引值获取对应列名中的数值
                            long _id = cursor.getLong(idIndex);
                            musicName = cursor.getString(titleIndex);
                            artist = cursor.getString(artistIndex);
                            packages = cursor.getString(uriIndex);
                            FLBMusic m = new FLBMusic(musicName, artist, packages);
                            flbMusicList.add(m);
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
