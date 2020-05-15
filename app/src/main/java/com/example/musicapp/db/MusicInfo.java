package com.example.musicapp.db;

import org.litepal.crud.LitePalSupport;

/**
 * Created by 贺宁昊 on 2020/5/11.
 */

public class MusicInfo extends LitePalSupport {

    private String music_name;

    private String music_player;

    private String music_package;

    private int image_no;

    private String lry_local;

    public String getLry_local() {
        return lry_local;
    }

    public void setLry_local(String lry_local) {
        this.lry_local = lry_local;
    }

    public int getImage_no() {
        return image_no;

    }

    public void setImage_no(int image_no) {
        this.image_no = image_no;
    }

    public String getMusic_name() {
        return music_name;
    }

    public void setMusic_name(String music_name) {
        this.music_name = music_name;
    }

    public String getMusic_player() {
        return music_player;
    }

    public void setMusic_player(String music_player) {
        this.music_player = music_player;
    }

    public String getMusic_package() {
        return music_package;
    }

    public void setMusic_package(String music_package) {
        this.music_package = music_package;
    }
}
