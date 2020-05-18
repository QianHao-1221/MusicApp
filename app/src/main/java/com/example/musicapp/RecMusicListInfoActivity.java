package com.example.musicapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicapp.adapter.RecAdapter;
import com.example.musicapp.db.FLBMusic;

import java.util.ArrayList;
import java.util.List;

public class RecMusicListInfoActivity extends AppCompatActivity {

    public static final String MUSIC_NAME = "music_name";

    public static final String MUSIC_IMAGE_ID = "music_image_id";

    private List<FLBMusic> data = new ArrayList<>();

    private String musicName, userNo;

    private RecAdapter adapter;

    private int userSituation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_music_list_info);

        Toast.makeText(RecMusicListInfoActivity.this, "长按可以添加到我喜欢哦", Toast.LENGTH_SHORT).show();

        initMusicList();

        //取出SharedPerferences对象data.xml文件中用户的数据
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        userNo = preferences.getString("userNo", "");
        userSituation = preferences.getInt("userSituation", 0);

        //接受传来的数据：音乐名、图片id
        Intent intent = getIntent();
        String musicName = intent.getStringExtra(MUSIC_NAME);
        int musicImageId = intent.getIntExtra(MUSIC_IMAGE_ID, 0);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.info_collapsing_toolbar);

        //获取toolbar图片和底下listview的实例
        ImageView musicImageView = (ImageView) findViewById(R.id.rec_music_info_image);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.info_list_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new RecAdapter(data);
        recyclerView.setAdapter(adapter);

        adapter.setLongClickListener(new RecAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(int position) {
                if (userSituation == 0) {
                    Toast.makeText(RecMusicListInfoActivity.this, "登录后使用此功能", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.addToFav(position, userNo);
                }
                return true;
            }

        });

        //获取toolbar实例
        Toolbar toolbar = (Toolbar) findViewById(R.id.info_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(musicName);//设置标题文字
        Glide.with(this).load(musicImageId).into(musicImageView);//设置标题图片
    }

    private void initMusicList() {
        data.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);// 默认排序顺序

                        int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);//获取列名对应的索引
                        int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);// 标题
                        int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);// 艺术家
                        int uriIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);// 文件路径

                        // 如果游标读取时还有下一个数据，读取
                        while (cursor.moveToNext()) {
                            // 根据索引值获取对应列名中的数值
                            long _id = cursor.getLong(idIndex);
                            musicName = cursor.getString(titleIndex);
                            String artist = cursor.getString(artistIndex);
                            String packages = cursor.getString(uriIndex);
                            FLBMusic m = new FLBMusic(musicName, artist, packages);
                            data.add(m);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
