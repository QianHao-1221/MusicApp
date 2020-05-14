package com.example.musicapp.layout;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.musicapp.MainActivity;
import com.example.musicapp.R;
import com.example.musicapp.adapter.UserMusicListAdapter;
import com.example.musicapp.db.MyMusicList;
import com.example.musicapp.db.RecMusicList;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class SecondLayout extends Fragment implements MainActivity.OnToFragmentListener {

    private View view;

    private Context mContext;

    private RecMusicList[] musicList;

    private List<RecMusicList> recMusicLists = new ArrayList<>();

    private UserMusicListAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ImageView addList;

    private String userNo = "0";

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

        setListener();

        addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final EditText editText = new EditText(view.getContext());
                if (!"0".equals(userNo)) {
                    new AlertDialog.Builder(view.getContext()).setTitle("新建歌单")
                            .setView(editText)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //按下确定键后的事件
                                    if ("".equals(editText.getText().toString())) {
                                        Toast.makeText(view.getContext(), "歌单名不能为空", Toast.LENGTH_SHORT).show();
                                    } else {
                                        adapter.addData(0, editText.getText().toString(), R.drawable.note);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MyMusicList myMusicList = new MyMusicList();
                                                myMusicList.setList_name(editText.getText().toString());
                                                myMusicList.setUser_no(userNo);
                                                myMusicList.save();
                                            }
                                        }).start();
                                    }
                                }
                            }).setNegativeButton("取消", null).show();
                } else {
                    Toast.makeText(view.getContext(), "您还没有登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //长按事件
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MyMusicList> lists = LitePal.where("user_no = ?",userNo).find(MyMusicList.class);
                for (MyMusicList m : lists){
                    RecMusicList r = new RecMusicList(m.getList_name(),R.drawable.note);
                    Log.e("OK",m.getList_name());
//                    recMusicLists.add(r);
                }
            }
        }).start();
//        for (int i = 0; i < musicList.length; i++) {
//            recMusicLists.add(musicList[i]);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void setListener() {
        if (mContext instanceof MainActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ((MainActivity) mContext).setOnToFragmentListener(this);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mContext instanceof MainActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ((MainActivity) mContext).setOnToFragmentListener(null);
            }
        }
    }

    @Override
    public void toFragment(String value) {
        userNo = value;
    }

}
