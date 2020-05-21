package com.example.musicapp.layout;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.R;
import com.example.musicapp.adapter.RecMusicListAdapter;
import com.example.musicapp.db.FLBMusic;
import com.example.musicapp.db.RecMusicList;
import com.example.musicapp.db.SysRecMusicList;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;


public class FirstLayout extends Fragment {

    private List<RecMusicList> recMusicLists = new ArrayList<>();

    private List<FLBMusic> flbMusicList = new ArrayList<>();

    private RecMusicListAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.layout1, container, false);

        initMusicList();

        //推荐列表获取
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.layout1_recycle_view);
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecMusicListAdapter(recMusicLists);
        recyclerView.setAdapter(adapter);

        //搜索列表获取
        searchView = (SearchView) view.findViewById(R.id.search_view);
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
                recMusicLists.clear();
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
                recMusicLists.clear();
                return false;
            }
        });

        //搜索栏关闭事件
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                initMusicList();
                return false;
            }
        });

        //下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRexMusicList();
            }
        });

        return view;
    }

    private void refreshRexMusicList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initMusicList();
                        swipeRefreshLayout.setRefreshing(false);
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<SysRecMusicList> lists = LitePal.where("list_name like ?", "%" + text + "%").order("list_name").find(SysRecMusicList.class);
                        for (SysRecMusicList sysRecMusicList : lists) {
                            RecMusicList recMusicList = new RecMusicList(sysRecMusicList.getList_name(), sysRecMusicList.getImageId());
                            recMusicLists.add(recMusicList);
                        }
                    }
                });
            }
        }).start();
    }

    private void initMusicList() {
        recMusicLists.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<SysRecMusicList> lists = LitePal.order("list_name").find(SysRecMusicList.class);
                        for (SysRecMusicList sysRecMusicList : lists) {
                            RecMusicList recMusicList = new RecMusicList(sysRecMusicList.getList_name(), sysRecMusicList.getImageId());
                            recMusicLists.add(recMusicList);
                        }
                    }
                });
            }
        }).start();
    }
}