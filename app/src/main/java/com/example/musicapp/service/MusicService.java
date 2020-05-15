package com.example.musicapp.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by West on 2015/11/10.
 */
public class MusicService extends Service {

    private ArrayList<String> musicDir = new ArrayList<>();

    private int musicIndex = -1;

    public final IBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        musicDir = (ArrayList<String>) intent.getSerializableExtra("data");
        return super.onStartCommand(intent, flags, startId);
    }

    public static MediaPlayer mp = new MediaPlayer();

    public MusicService() {
        try {
            mp.setDataSource("/storage/emulated/0/music.mp3");
            //mp.setDataSource(Environment.getDataDirectory().getAbsolutePath()+"/You.mp3");
            mp.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //外部访问此方法，主要用来播放歌曲
    public void play(String path) {
        try {
            mp.reset();
            mp.setDataSource(path);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.seekTo(0);
        mp.start();
        mp.setLooping(true);
    }

    public void playOrPause() {
        if (mp.isPlaying()) {
            mp.pause();
        } else {
            mp.start();
        }
    }

    public void nextMusic() {
        if (mp != null && musicIndex < musicDir.size() - 1) {
            mp.stop();
            try {
                mp.reset();
                mp.setDataSource(musicDir.get(musicIndex + 1));
                musicIndex++;
                mp.prepare();
                mp.seekTo(0);
                mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void preMusic() {
        if (mp != null && musicIndex > 0) {
            mp.stop();
            try {
                mp.reset();
                mp.setDataSource(musicDir.get(musicIndex - 1));
                musicIndex--;
                mp.prepare();
                mp.seekTo(0);
                mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //随机播放
    public void noMusic() {
        if (mp != null && musicIndex > 0) {
            mp.stop();
            try {
                mp.reset();
                Random random = new Random();
                int index = random.nextInt(musicDir.size());
                mp.setDataSource(musicDir.get(index));
                mp.prepare();
                mp.seekTo(0);
                mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
        super.onDestroy();
    }

    /**
     * onBind 是 Service 的虚方法，因此我们不得不实现它。
     * 返回 null，表示客服端不能建立到此服务的连接。
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

}
