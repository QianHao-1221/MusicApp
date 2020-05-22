package com.example.musicapp.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.example.musicapp.db.History;
import com.example.musicapp.db.MusicInfo;

import org.litepal.LitePal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by West on 2015/11/10.
 */
public class MusicService extends Service {

    private ArrayList<String> musicDir = new ArrayList<>();

    private int musicIndex = 0;

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
        musicDir.add("/storage/emulated/0/1.mp3");
        try {
            mp.setDataSource(musicDir.get(0));
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
        saveToHistory(path);
        mp.setLooping(true);
    }

    public void playOrPause() {
        if (mp.isPlaying()) {
            mp.pause();
        } else {
            mp.start();
            saveToHistory(musicDir.get(musicIndex));
        }
    }

    public String path() {
        String path;
        if (musicIndex - 1 == -1) {
            path = musicDir.get(musicIndex);
        } else {
            path = musicDir.get(musicIndex - 1);
        }
        return path;
    }

    public String currentPath() {
        return musicDir.get(musicIndex);
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
                saveToHistory(musicDir.get(musicIndex));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void preMusic() {
        //上一曲
        if (mp != null && musicIndex > 0) {
            mp.stop();//暂停当前歌曲
            try {
                mp.reset();//重置播放器
                mp.setDataSource(musicDir.get(musicIndex - 1));
                musicIndex--;
                mp.prepare();
                mp.seekTo(0);
                mp.start();
                saveToHistory(musicDir.get(musicIndex));
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
                musicIndex = random.nextInt(musicDir.size());
                mp.setDataSource(musicDir.get(musicIndex));
                mp.prepare();
                mp.seekTo(0);
                mp.start();
                saveToHistory(musicDir.get(musicIndex));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //    //当前歌曲存储到History表中
    private void saveToHistory(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                Date date = new Date(System.currentTimeMillis());
                History history = new History();
                List<History> histories = LitePal.where("path = ?", path).find(History.class);
                List<MusicInfo> musicInfos = LitePal.where("music_package = ?", path).find(MusicInfo.class);
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
                    history.updateAll("path = ?", path);
                }
            }
        }).start();
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
