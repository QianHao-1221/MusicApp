package com.example.musicapp.db;

/**
 * Created by 贺宁昊 on 2020/5/9.
 */

public class FLBMusic {

    private String musicName;

    private String singerName;

    private String pageName;

    public FLBMusic(String musicName, String singerName, String pageName) {
        this.musicName = musicName;
        this.singerName = singerName;
        this.pageName = pageName;
    }

    public String getMusicName() {
        return musicName;
    }

    public String getSingerName() {
        return singerName;
    }

    public String getPageName() {
        return pageName;
    }
}
