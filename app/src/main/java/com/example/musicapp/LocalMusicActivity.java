package com.example.musicapp;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.adapter.FLBAdapter;
import com.example.musicapp.adapter.ListAdapter;
import com.example.musicapp.db.FLBMusic;
import com.example.musicapp.db.MusicInfo;
import com.example.musicapp.service.MusicService;
import com.example.musicapp.utils.MusicUtils;

import java.util.ArrayList;
import java.util.List;

public class LocalMusicActivity extends AppCompatActivity {

    private List<MusicInfo> musicList = new ArrayList<MusicInfo>();
    private ListAdapter mListAdapter;
    private ListView mListView;
    //记录长按的列表项坐标
    private int currentSel;

    private TextView listTitle;

    //自定义Binder对象 用于调用服务中的方法
    private MyBinderInterface myBinder;
    //自定义服务连接对象
    private MyServiceConnection conn;
    //是否正在播放
    private boolean isPlaying = false;
    private Handler handler = new Handler();
    //更新线程用于更新进度条
    private Runnable updateThread = new Runnable() {
        @Override
        public void run() {
            if (myBinder != null) {
                try {
                    if (myBinder.isPlaying()) {
                        int duration = myBinder.getDuration();
                        int currentPos = myBinder.getCurrentPosition();

                        int prg_sec = currentPos / 1000;
                        int max_sec = duration / 1000;
                        if (prg_sec == max_sec) {
                            myBinder.PlayNext();
                            updateState();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            handler.post(updateThread);
        }
    };

    private List<FLBMusic> flbMusicList = new ArrayList<>();

    private FLBAdapter adapter;

    //定义服务连接
    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MyBinderInterface) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
    //更新播放状态
    private void updateState() {
        int index = myBinder.getCurrentIndex();
        mListAdapter.setFocusItemPos(index);
        String currentMusicName = musicList.get(index).getMusic_title();
        isPlaying = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);

//        initMusicList();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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
                searchView.setIconified(false);
            }
        });
        musicList = MusicUtils.ResolveMusicToList(getApplicationContext());
        //获取视图
        initView();
        //设置列表标题
        String title = getResources().getString(R.string.title_string).toString();
        title += "（总数：" + musicList.size() + "）";
        listTitle.setText(title);
        //为mListView注册上下文菜单
        registerForContextMenu(mListView);
        conn = new MyServiceConnection();
        //绑定服务
        bindService(new Intent(this, MusicService.class), conn, Context.BIND_AUTO_CREATE);

        handler.post(updateThread);
    }

//    private void initMusicList() {
//        flbMusicList.clear();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        List<Music> musics = LitePal.order("music_name").find(Music.class);
//                        for (Music music : musics) {
//                            FLBMusic[] m = {new FLBMusic(music.getMusic_name(), music.getMusic_player(), music.getMusic_package())};
//                            for (int i = 0; i < m.length; i++) {
//                                flbMusicList.add(m[i]);
//                            }
//                        }
//                    }
//                });
//            }
//        }).start();
//    }

    private void initView() {
        listTitle = (TextView) findViewById(R.id.music_list_title);

        mListView = (ListView) findViewById(R.id.music_list);
        mListAdapter = new ListAdapter(LocalMusicActivity.this, musicList);
        mListView.setAdapter(mListAdapter);

        setListener();
    }

    //设置监听事件
    private void setListener() {
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mListView.showContextMenu();
                currentSel = position;
                return true;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long i) {
                myBinder.setCurrentIndex(position);
                myBinder.seekTo(0);
                myBinder.Play();
                mListAdapter.setFocusItemPos(position);
                updateState();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 0, 0, R.string.menu_detail);
        menu.add(0, 1, 1, R.string.menu_play);
        super.onCreateContextMenu(menu, view, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                StringBuilder msgBuilder = new StringBuilder();
                msgBuilder.append("文件名：" + musicList.get(currentSel).getMusic_name() + "\n");
                msgBuilder.append("路  径：" + musicList.get(currentSel).getMusic_path() + "\n");
                msgBuilder.append("时  长：" + musicList.get(currentSel).getMusic_duration() / 1000 + " s\n");
                String title = "文件详情";
                new AlertDialog.Builder(LocalMusicActivity.this)
                        .setIcon(R.drawable.note)
                        .setTitle(title)
                        .setMessage(msgBuilder.toString())
                        .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
                break;
            case 1:
                //不处于播放状态 或者 选择的歌曲和正在播放的歌曲不是同一首 则更新状态且播放
                if (isPlaying == false || currentSel != myBinder.getCurrentIndex()) {
                    myBinder.setCurrentIndex(currentSel);
                    updateState();
                    myBinder.Play();
                }
                //提示选择的歌曲已经在播放了
                else {
                    Toast.makeText(LocalMusicActivity.this, R.string.str_playing, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean retValue = super.onCreateOptionsMenu(menu);
        return retValue;
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
