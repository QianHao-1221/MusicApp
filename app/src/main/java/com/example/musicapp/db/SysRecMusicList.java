package com.example.musicapp.db;

import org.litepal.crud.LitePalSupport;

/**
 * Created by 贺宁昊 on 2020/5/14.
 */

public class SysRecMusicList extends LitePalSupport {

    String list_name;

    int imageId;

    public String getList_name() {
        return list_name;
    }

    public void setList_name(String list_name) {
        this.list_name = list_name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
