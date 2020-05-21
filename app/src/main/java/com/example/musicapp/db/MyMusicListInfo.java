package com.example.musicapp.db;

import org.litepal.crud.LitePalSupport;

/**
 * Created by 贺宁昊 on 2020/5/20.
 */

public class MyMusicListInfo extends LitePalSupport {

    private String user_no;

    private String list_name;

    private String music_name;

    private String singer_name;

    private String path;

    public String getUser_no() {
        return user_no;
    }

    public void setUser_no(String user_no) {
        this.user_no = user_no;
    }

    public String getList_name() {
        return list_name;
    }

    public void setList_name(String list_name) {
        this.list_name = list_name;
    }

    public String getMusic_name() {
        return music_name;
    }

    public void setMusic_name(String music_name) {
        this.music_name = music_name;
    }

    public String getSinger_name() {
        return singer_name;
    }

    public void setSinger_name(String singer_name) {
        this.singer_name = singer_name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
