package com.example.musicapp.layout;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.musicapp.R;
import com.example.musicapp.adapter.UserMusicListAdapter;
import com.example.musicapp.db.RecMusicList;

import java.util.ArrayList;
import java.util.List;

public class SecondLayout extends Fragment {

    private View view;

    private RecMusicList[] musicList = {
            new RecMusicList("Music1", R.drawable.note), new RecMusicList("Music2", R.drawable.test),
            new RecMusicList("Music3", R.drawable.test), new RecMusicList("Music4", R.drawable.test),
            new RecMusicList("Music5", R.drawable.test), new RecMusicList("Music6", R.drawable.test),
            new RecMusicList("Music7", R.drawable.test), new RecMusicList("Music8", R.drawable.test),
            new RecMusicList("Music9", R.drawable.test), new RecMusicList("Music10", R.drawable.test)};

    private List<RecMusicList> recMusicLists = new ArrayList<>();

    private UserMusicListAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ImageView addList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout2, container, false);

        initMusicList();

        addList = (ImageView) view.findViewById(R.id.add_list);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.user_music_list);
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserMusicListAdapter(recMusicLists);
        recyclerView.setAdapter(adapter);

        addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final EditText editText = new EditText(view.getContext());
                new AlertDialog.Builder(view.getContext()).setTitle("新建歌单")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //按下确定键后的事件
                                adapter.addData(0, editText.getText().toString(), R.drawable.duck1);
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });

        adapter.setLongClickListener(new UserMusicListAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(final int position) {
                new AlertDialog.Builder(view.getContext()).setTitle("此操作将会删除歌单")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //按下确定键后的事件
                                adapter.removeData(position);
                            }
                        }).setNegativeButton("取消", null).show();
                return true;
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_2);
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
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void initMusicList() {
        recMusicLists.clear();
        for (int i = 0; i < musicList.length; i++) {
            recMusicLists.add(musicList[i]);
        }
    }

}
