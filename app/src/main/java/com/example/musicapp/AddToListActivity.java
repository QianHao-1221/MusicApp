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

import com.example.musicapp.adapter.UserMusicListAdapter;
import com.example.musicapp.db.MyMusicList;
import com.example.musicapp.db.RecMusicList;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class AddToListActivity extends AppCompatActivity {

    private List<RecMusicList> recMusicLists = new ArrayList<>();

    private UserMusicListAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private String userNo;

    private int position;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_list);

        //取出从RecMusicListInfo传过来的数据
        Intent intent = getIntent();
        userNo = intent.getStringExtra("userNo");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.add_to_list_recycle_view);//获取Recycleview
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);//选择显示的方式
        recyclerView.setLayoutManager(layoutManager);//显示方式传入recyycleview
        adapter = new UserMusicListAdapter(recMusicLists, 0);
        recyclerView.setAdapter(adapter);//传入适配器并显示

        adapter.setLongClickListener(new UserMusicListAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(int position) {
                return false;
            }
        });

        initMusicList();

        //获取Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        searchView = (SearchView) findViewById(R.id.add_to_list_search_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            searchView.findViewById(android.support.v7.appcompat.R.id.search_plate).setBackground(null);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            searchView.findViewById(android.support.v7.appcompat.R.id.submit_area).setBackground(null);
        }

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recMusicLists.clear();
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
                recMusicLists.clear();
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

        adapter.setLongClickListener(new UserMusicListAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(int position) {
                return false;
            }
        });

        //下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_add_to_list);
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
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    public void initMusicList() {
        recMusicLists.clear();//清空数组
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<MyMusicList> lists = LitePal.where("user_no = ?", userNo).order("list_name").find(MyMusicList.class);
                        for (MyMusicList m : lists) {
                            adapter.addData(0, m.getList_name(), R.drawable.note);
                        }
                    }
                });
            }
        }).start();
    }

    private void search(final String text) {
        recMusicLists.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<MyMusicList> lists = LitePal.where("list_name like ?", "%" + text + "%").order("list_name").find(MyMusicList.class);
                        for (MyMusicList myMusicList : lists) {
                            RecMusicList recMusicList = new RecMusicList(myMusicList.getList_name(), R.drawable.note);
                            recMusicLists.add(recMusicList);
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

    @Override
    public void finish() {
        super.finish();
    }
}
