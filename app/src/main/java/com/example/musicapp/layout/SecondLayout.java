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

    private List<RecMusicList> recMusicLists = new ArrayList<>();

    private UserMusicListAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ImageView favPic, addList;

    private String userNo = "0";

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout2, container, false);

        addList = (ImageView) view.findViewById(R.id.add_list);
        favPic = (ImageView) view.findViewById(R.id.fav_pic);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.user_music_list);//获取Recycleview
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 1);//选择显示的方式
        recyclerView.setLayoutManager(layoutManager);//显示方式传入recyycleview
        adapter = new UserMusicListAdapter(recMusicLists, 1);
        recyclerView.setAdapter(adapter);//传入适配器并显示

        initMusicList();

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
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //歌单信息存到数据库
                                                        List<MyMusicList> myMusicLists = LitePal.where("user_no = ? and list_name = ?", userNo, editText.getText().toString()).find(MyMusicList.class);
                                                        if (myMusicLists.size() == 0) {
                                                            MyMusicList myMusicList = new MyMusicList();
                                                            myMusicList.setList_name(editText.getText().toString());
                                                            myMusicList.setUser_no(userNo);
                                                            myMusicList.save();
                                                            Toast.makeText(view.getContext(), "歌单创建成功", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(view.getContext(), "歌单不能重名", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
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
                                adapter.removeData(position, userNo);
                                Toast.makeText(view.getContext(), "删除成功！", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("取消", null).show();
                return true;
            }
        });

        //下拉刷新
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
        //下拉刷新操作
        new Thread(new Runnable() {
            @Override
            public void run() {
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

    public void initMusicList() {
        recMusicLists.clear();//清空数组
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
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

    //MainActivity向Fragment传递数据的方式
    //底下四个都是关键
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

    //销毁后，不再传值
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
