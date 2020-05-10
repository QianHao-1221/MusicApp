package com.example.musicapp.db;

/**
 * Created by 贺宁昊 on 2020/4/27.
 */

public class RecMusicList {

    private String name;

    private int imageId;

    public RecMusicList(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public String getName() {
        return name;
    }
}
