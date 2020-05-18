package com.example.musicapp.db;

/**
 * Created by 贺宁昊 on 2020/5/18.
 */

public class CommentController {

    private int imgId;

    private String userName;

    private String date;

    private String text;

    public CommentController(int imgId, String userName, String date, String text) {
        this.imgId = imgId;
        this.userName = userName;
        this.date = date;
        this.text = text;
    }

    public int getImgId() {
        return imgId;
    }

    public String getUserName() {
        return userName;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }
}
