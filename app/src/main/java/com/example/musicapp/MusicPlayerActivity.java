package com.example.musicapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.service.MusicPlayerService;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView playWay, playBtn;

    private int pickWay = 1;

    private Intent intent3;

    private int musicSituation = 0;//0:未播放 1:开始播放

    private TextView currentTime, allTime;

    private String currentT, allT;

    private MusicPlayerService.MyBinder musicControl;
    private MyConnection conn;
    private SeekBar seekBar;
    private static final int UPDATE_PROGRESS = 0;


    private SimpleDateFormat format = new SimpleDateFormat("mm:ss");

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    updateProgress();
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        playWay = (ImageView) findViewById(R.id.play_way);
        playBtn = (ImageView) findViewById(R.id.player_main_btn);
        seekBar = (SeekBar) findViewById(R.id.player_bar);

        currentTime = (TextView) findViewById(R.id.current_time);
        allTime = (TextView) findViewById(R.id.all_time);

        intent3 = new Intent(this, MusicPlayerService.class);
        conn = new MyConnection();

        //使用混合的方法开启服务，
        startService(intent3);
        bindService(intent3, conn, BIND_AUTO_CREATE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                //进度条改变
                                if (fromUser) {
                                    musicControl.seekTo(progress);
                                    format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                                    currentT = format.format(musicControl.getCurrentPosition());
                                    currentTime.setText(currentT);
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                                //开始触摸进度条
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                //停止触摸进度条
                            }

                        });
                    }
                });
            }
        }).start();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.player_close);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    //服务启动完成后会进入到这个方法
    private class MyConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获得service中的MyBinder
            musicControl = (MusicPlayerService.MyBinder) service;
            //更新按钮的文字
            updatePlayImage();
            //设置进度条的最大值
            seekBar.setMax(musicControl.getDuration());
            //设置进度条的进度
            seekBar.setProgress(musicControl.getCurrentPosition());

            format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            allT = format.format(musicControl.getDuration());
            allTime.setText(allT);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
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
                musicControl.play();
                updatePlayImage();
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

    //更新进度条
    private void updateProgress() {
        format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        currentT = format.format(musicControl.getCurrentPosition());
        currentTime.setText(currentT);
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int currentPosition = musicControl.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        //使用Handler每500毫秒更新一次进度条
                        handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
                    }
                });
            }
        }).start();
    }

    //更新图片
    public void updatePlayImage() {
        if (musicControl.isPlaying()) {
            playBtn.setImageResource(R.drawable.pause);
            musicSituation = 1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(UPDATE_PROGRESS);
                }
            }).start();
        } else {
            playBtn.setImageResource(R.drawable.start);
            musicSituation = 0;
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
    protected void onResume() {
        super.onResume();
        //进入到界面后开始更新进度条
        if (musicControl != null) {
            format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            currentT = format.format(musicControl.getCurrentPosition());
            currentTime.setText(currentT);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(UPDATE_PROGRESS);
                }
            }).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("musicSituation", musicSituation);
        setResult(RESULT_OK, intent);
        super.finish();
    }
}