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

import com.example.musicapp.service.MusicService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView playWay, lastMusic, playBtn, nextMusic;

    private int pickWay = 1;

    public Intent intent;

    private int musicSituation = 0, playwaySituation = 1;//0:未播放 1:开始播放

    private TextView currentTime, allTime, music, singer;

    private MusicService musicService;

    private SeekBar seekBar;

    private SimpleDateFormat time = new SimpleDateFormat("m:ss");

    public void stop() {
        stopService(intent);
    }

    private String uri, musicName, artist;

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

        bindServiceConnection();

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


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.player_close);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    protected void onResume() {
        if (musicService.mp.isPlaying()) {
            playBtn.setImageResource(R.drawable.pause);
            musicSituation = 1;
        } else {
            playBtn.setImageResource(R.drawable.start);
            musicSituation = 0;
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
                if (pickWay == 1) {
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
                PlayWay();
                break;
            case R.id.next_music:
                musicService.nextMusic();
                PlayWay();
                break;
            default:
        }
    }

    private void setPlayWay(int way) {
        switch (way) {
            case 1:
                playwaySituation = 1;
                playWay.setImageResource(R.drawable.shunxu);
                PlayWay();
                Toast.makeText(MusicPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                playwaySituation = 2;
                playWay.setImageResource(R.drawable.suiji);
                PlayWay();
                Toast.makeText(MusicPlayerActivity.this, "随机播放", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                playwaySituation = 3;
                playWay.setImageResource(R.drawable.danqu);
                PlayWay();
                Toast.makeText(MusicPlayerActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
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
        intent.putExtra("musicSituation", musicSituation);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    private void PlayWay() {
        switch (playwaySituation) {
            case 1:
                musicService.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        musicService.nextMusic();
                    }
                });
                playwaySituation = 1;
                break;
            case 2:
                musicService.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        musicService.noMusic();
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
}