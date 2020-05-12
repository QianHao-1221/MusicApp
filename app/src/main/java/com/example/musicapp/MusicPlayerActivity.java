package com.example.musicapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView playWay, lastMusic, playBtn, nextMusic;

    private int pickWay = 1;

    public Intent intent;

    private int musicSituation = 0;//0:未播放 1:开始播放

    private TextView currentTime, allTime;

    private MusicService musicService;

    private SeekBar seekBar;

    private SimpleDateFormat time = new SimpleDateFormat("m:ss");

    public void stop(){
        stopService(intent);
    }

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
        Bundle bundle = new Bundle();
        bundle.putStringArray("data", new String[]{"/storage/emulated/legacy/music.mp3", "/storage/emulated/legacy/1.mp3",
                "/storage/emulated/legacy/2.mp3", "/storage/emulated/legacy/3.mp3"});
        intent.putExtras(bundle);

        startService(intent);
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
            handler.postDelayed(runnable, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        musicService = new MusicService();

        bindServiceConnection();

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
                break;
            case R.id.next_music:
                musicService.nextMusic();
                break;
            default:
        }
    }

    private void setPlayWay(int way) {
        switch (way) {
            case 1:
                playWay.setImageResource(R.drawable.shunxu);
                Toast.makeText(MusicPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                playWay.setImageResource(R.drawable.suiji);
                Toast.makeText(MusicPlayerActivity.this, "随机播放", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                playWay.setImageResource(R.drawable.danqu);
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
}