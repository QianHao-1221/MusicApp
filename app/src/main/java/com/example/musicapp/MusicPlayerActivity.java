package com.example.musicapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.db.History;
import com.example.musicapp.db.MusicInfo;
import com.example.musicapp.service.MusicService;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView playWay, lastMusic, playBtn, nextMusic, musicImg;

    private int pickWay = 1;

    public Intent intent, intent2;

    private int playwaySituation = 1, flag = 0, userSituation = 0, playflag, way;//0:未播放 1:开始播放

    private TextView currentTime, allTime, music, singer;

    private MusicService musicService;

    private SeekBar seekBar;

    private SimpleDateFormat time = new SimpleDateFormat("m:ss");

    private String uri, musicName, artist, userNo;

    private String path;

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicService = ((MusicService.MyBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
        }
    };

    private void bindServiceConnection() {
        intent = new Intent(MusicPlayerActivity.this, MusicService.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);// 默认排序顺序
                // 如果游标读取时还有下一个数据，读取

                int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);//获取列名对应的索引
                int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);// 标题
                int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);// 艺术家
                int uriIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);// 文件路径
                Bundle bundle = new Bundle();

                ArrayList<String> musics = new ArrayList<>();
                while (cursor.moveToNext()) {
                    // 根据索引值获取对应列名中的数值
                    long _id = cursor.getLong(idIndex);
                    musicName = cursor.getString(titleIndex);
                    artist = cursor.getString(artistIndex);
                    uri = cursor.getString(uriIndex);
                    musics.add(uri);
                }
                bundle.putSerializable("data", musics);
                intent.putExtras(bundle);
                startService(intent);
            }
        }).start();
        bindService(intent, sc, this.BIND_AUTO_CREATE);
    }

    public android.os.Handler handler = new android.os.Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (musicService.mp.isPlaying()) {
                playBtn.setImageResource(R.drawable.pause);
            } else {
                playBtn.setImageResource(R.drawable.start);
            }
            currentTime.setText(time.format(musicService.mp.getCurrentPosition()));
            allTime.setText(time.format(musicService.mp.getDuration()));
            seekBar.setProgress(musicService.mp.getCurrentPosition());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        musicService.mp.seekTo(seekBar.getProgress());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            handler.postDelayed(runnable, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        musicService = new MusicService();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.player_close);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        bindServiceConnection();

        intent2 = getIntent();
        userNo = intent2.getStringExtra("userNo");
        playwaySituation = intent2.getIntExtra("playWay", 1);
        pickWay = intent2.getIntExtra("pickWay", 1);
        flag = intent2.getIntExtra("flag", 1);
        userSituation = intent2.getIntExtra("userSituation", 0);

        path = intent2.getStringExtra("flb_path");
        playflag = intent2.getIntExtra("flb_playFlag", 0);
        way = intent2.getIntExtra("flb_way", 1);

        if (path != null && playflag == 1) {
            if (way == 1) {
                getInfo(path);
                flag = 1;
            } else {
                change();
            }
        } else {
            change();
        }

        musicImg = (ImageView) findViewById(R.id.player_song_pic);
        music = (TextView) findViewById(R.id.player_song_name);
        singer = (TextView) findViewById(R.id.player_singer_name);

//        MusicInfo musicInfo = new MusicInfo();
//        musicInfo.setMusic_name("出山");
//        musicInfo.setMusic_player("花粥");
//        musicInfo.setMusic_package("/storage/emulated/0/3.mp3");
//        musicInfo.setImage_no(R.drawable.panda);
//        musicInfo.setLry_local("/storage/emulated/0/3.mp3");
//        musicInfo.save();

        playWay = (ImageView) findViewById(R.id.play_way);
        seekBar = (SeekBar) findViewById(R.id.player_bar);
        seekBar.setProgress(musicService.mp.getCurrentPosition());
        seekBar.setMax(musicService.mp.getDuration());

        playBtn = (ImageView) findViewById(R.id.player_main_btn);
        currentTime = (TextView) findViewById(R.id.current_time);
        allTime = (TextView) findViewById(R.id.all_time);

        PlayWay();
        setPlayWay(pickWay);
    }

    @Override
    protected void onResume() {
        if (!musicService.mp.isPlaying()) {
            playBtn.setImageResource(R.drawable.start);
        } else {
            playBtn.setImageResource(R.drawable.start);
        }

        seekBar.setProgress(musicService.mp.getCurrentPosition());
        seekBar.setMax(musicService.mp.getDuration());
        handler.post(runnable);
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.player_song_pic:
                startActivityForResult(new Intent(MusicPlayerActivity.this, LyricActivity.class), 1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.play_way:
                if (pickWay == 1 || pickWay == 0) {
                    setPlayWay(2);
                    pickWay = 2;
                } else if (pickWay == 2) {
                    setPlayWay(3);
                    pickWay = 3;
                } else if (pickWay == 3) {
                    setPlayWay(1);
                    pickWay = 1;
                }
                break;
            case R.id.player_main_btn:
                musicService.playOrPause();
                break;
            case R.id.last_music:
                musicService.preMusic();
                way = 0;
                change();
                PlayWay();
                seekBar.setMax(musicService.mp.getDuration());
                break;
            case R.id.next_music:
                flag = 1;
                way = 0;
                musicService.nextMusic();
                change();
                PlayWay();
                seekBar.setMax(musicService.mp.getDuration());
                break;
            case R.id.comment:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<MusicInfo> musicInfos = LitePal.where("music_package = ?", musicService.currentPath()).find(MusicInfo.class);
                        for (MusicInfo musicInfo : musicInfos) {
                            Intent intent = new Intent(MusicPlayerActivity.this, CommentActivity.class);
                            intent.putExtra("userSituation", userSituation);
                            intent.putExtra("musicId", musicInfo.getId());
                            startActivity(intent);
                            overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);
                        }
                    }
                }).start();
                break;
            case R.id.download:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<MusicInfo> musicInfos = LitePal.where("music_package = ?", musicService.currentPath()).find(MusicInfo.class);
                                if (musicInfos.size() == 0) {
                                    //开始下载
                                } else {
                                    Toast.makeText(MusicPlayerActivity.this, "歌曲已经下载过了", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).start();
                break;
            default:
        }
    }

    private void setPlayWay(int way) {
        switch (way) {
            case 0:
            case 1:
                playwaySituation = 1;
                playWay.setImageResource(R.drawable.shunxu);
                PlayWay();
                break;
            case 2:
                playwaySituation = 2;
                playWay.setImageResource(R.drawable.suiji);
                PlayWay();
                break;
            case 3:
                playwaySituation = 3;
                playWay.setImageResource(R.drawable.danqu);
                PlayWay();
                break;
            default:
        }
        seekBar.setMax(musicService.mp.getDuration());
    }

    private void PlayWay() {
        switch (playwaySituation) {
            case 1:
                musicService.mp.setLooping(false);
                musicService.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        musicService.nextMusic();
                        change();
                        seekBar.setMax(musicService.mp.getDuration());
                        if (flag == 1) {
                            saveToHistory();
                        } else {
                            Toast.makeText(MusicPlayerActivity.this, "这首歌为系统推荐，不计入试听历史", Toast.LENGTH_SHORT).show();
                            flag = 1;
                        }
                        way = 0;
                    }
                });
                playwaySituation = 1;
                break;
            case 2:
                musicService.mp.setLooping(false);
                musicService.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        musicService.noMusic();
                        change();
                        seekBar.setMax(musicService.mp.getDuration());
                        if (flag == 1) {
                            saveToHistory();
                        } else {
                            Toast.makeText(MusicPlayerActivity.this, "这首歌为系统推荐，不计入试听历史", Toast.LENGTH_SHORT).show();
                            flag = 1;
                        }
                        way = 0;
                    }
                });
                playwaySituation = 2;
                break;
            case 3:
                musicService.mp.setLooping(true);
                playwaySituation = 3;
                break;
            default:
        }
    }

    //    //当前歌曲存储到History表中
    private void saveToHistory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                        Date date = new Date(System.currentTimeMillis());
                        History history = new History();
                        List<History> histories = LitePal.where("path = ?", musicService.path()).find(History.class);
                        List<MusicInfo> musicInfos = LitePal.where("music_package = ?", musicService.path()).find(MusicInfo.class);
                        if (histories.size() == 0) {
                            for (MusicInfo musicInfo : musicInfos) {
                                history.setMusic_name(musicInfo.getMusic_name());
                                history.setSinger_name(musicInfo.getMusic_player());
                                history.setTime(simpleDateFormat.format(date));
                                history.setPath(musicInfo.getMusic_package());
                                history.save();
                            }
                        } else {
                            history.setTime(simpleDateFormat.format(date));
                            history.updateAll("path = ?", musicService.path());
                        }
                    }
                });
            }
        }).start();
    }

    public void change() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<MusicInfo> musicInfos = LitePal.where("music_package = ?", musicService.currentPath()).find(MusicInfo.class);
                        for (MusicInfo musicInfo : musicInfos) {
                            musicImg.setImageResource(musicInfo.getImage_no());
                            music.setText(musicInfo.getMusic_name());
                            singer.setText(musicInfo.getMusic_player());
                        }
                    }
                });
            }
        }).start();
    }

    public void getInfo(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<MusicInfo> musicInfos = LitePal.where("music_package = ?", path).find(MusicInfo.class);
                        for (MusicInfo musicInfo : musicInfos) {
                            musicImg.setImageResource(musicInfo.getImage_no());
                            music.setText(musicInfo.getMusic_name());
                            singer.setText(musicInfo.getMusic_player());
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
    public void onDestroy() {
        unbindService(sc);
        super.onDestroy();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("playWay", playwaySituation);
        intent.putExtra("pickWay", pickWay);
        intent.putExtra("flag", flag);
        intent.putExtra("flb_way", way);
        setResult(RESULT_OK, intent);
        super.finish();
    }
}