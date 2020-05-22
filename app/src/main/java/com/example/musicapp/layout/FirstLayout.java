package com.example.musicapp.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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