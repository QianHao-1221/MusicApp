package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bumptech.glide.Glide;

public class UserMusicListActivity extends AppCompatActivity {

    public static final String MUSIC_NAME = "music_name";

    public static final String MUSIC_IMAGE_ID = "music_image_id";

    private String[] data = {"1", "2", "1",
            "这是歌名啊位为iwe", "这是歌名啊位为iwe", "这是歌名啊位为iwe",
            "这是歌名啊位为iwe", "这是歌名啊位为iwe", "这是歌名啊位为iwe",
            "这是歌名啊位为iwe", "这是歌名啊位为iwe", "这是歌名啊位为iwe",
            "这是歌名啊位为iwe", "这是歌名啊位为iwe", "这是歌名啊位为iwe",
            "这是歌名啊位为iwe", "这是歌名啊位为iwe", "这是歌名啊位为iwe",
            "1", "2", "3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_music_list);

        //接受传来的数据：音乐名、图片id
        Intent intent = getIntent();
        String musicName = intent.getStringExtra(MUSIC_NAME);
        int musicImageId = intent.getIntExtra(MUSIC_IMAGE_ID, 0);

        //获取CollapsingToolbarLayout实例
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.user_list_collapsing_toolbar);


        ImageView musicImageView = (ImageView) findViewById(R.id.user_list_image);
        //获取toolbar图片和底下listview的实例
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                UserMusicListActivity.this, android.R.layout.simple_list_item_1, data);
        ListView listView = (ListView) findViewById(R.id.user_list_view);
        listView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(listView);

        //listView点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("OK", "" + data[i]);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.user_list_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(musicName);
        Glide.with(this).load(musicImageId).into(musicImageView);
    }

    //嵌套Listview用的方法
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
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
