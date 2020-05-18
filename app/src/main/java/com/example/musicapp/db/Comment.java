package com.example.musicapp.db;

import org.litepal.crud.LitePalSupport;

/**
 * Created by 贺宁昊 on 2020/5/18.
 */

public class Comment extends LitePalSupport {

    private int music_id;

    private String user_name;

    private int user_img;

    private String comment_text;

    private int agree;

    private String date;

    private String user_no;

    public int getMusic_id() {
        return music_id;
    }

    public void setMusic_id(int music_id) {
        this.music_id = music_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getUser_img() {
        return user_img;
    }

    public void setUser_img(int user_img) {
        this.user_img = user_img;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public int getAgree() {
        return agree;
    }

    public void setAgree(int agree) {
        this.agree = agree;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser_no() {
        return user_no;
    }

    public void setUser_no(String user_no) {
        this.user_no = user_no;
    }
}
