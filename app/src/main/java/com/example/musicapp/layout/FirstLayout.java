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
import com.example.musicapp.db.RecMusicList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class FirstLayout extends Fragment {

    private RecMusicList[] musicList = {
            new RecMusicList("Music1", R.drawable.test), new RecMusicList("Music1", R.drawable.test),
            new RecMusicList("Music1", R.drawable.test), new RecMusicList("Music1", R.drawable.test),
            new RecMusicList("Music1", R.drawable.test), new RecMusicList("Music1", R.drawable.test),
            new RecMusicList("Music1", R.drawable.test), new RecMusicList("Music1", R.drawable.test),
            new RecMusicList("Music1", R.drawable.test), new RecMusicList("Music1", R.drawable.test)
    };

    private List<RecMusicList> recMusicLists = new ArrayList<>();

    private RecMusicListAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout1, container, false);

        initMusicList();

        //推荐列表获取
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.layout1_recycle_view);
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecMusicListAdapter(recMusicLists);
        recyclerView.setAdapter(adapter);

        searchView = (SearchView) view.findViewById(R.id.search_view);
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
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
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

    private void initMusicList() {
        recMusicLists.clear();
        for (int i = 0; i < 50; i++) {
            Random random = new Random();
            int index = random.nextInt(musicList.length);
            recMusicLists.add(musicList[index]);
        }
    }
}