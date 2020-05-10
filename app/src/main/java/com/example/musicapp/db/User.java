package com.example.musicapp.db;

import org.litepal.crud.LitePalSupport;

public class User extends LitePalSupport {

    private String user_no;

    private String user_name;

    private String user_password;

    private String super_password;

    private int pic_id;

    private String custom_color;

    private int user_level;

    public String getUser_no() {
        return user_no;
    }

    public void setUser_no(String user_no) {
        this.user_no = user_no;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getSuper_password() {
        return super_password;
    }

    public void setSuper_password(String super_password) {
        this.super_password = super_password;
    }

    public int getPic_id() {
        return pic_id;
    }

    public void setPic_id(int pic_id) {
        this.pic_id = pic_id;
    }

    public String getCustom_color() {
        return custom_color;
    }

    public void setCustom_color(String custom_color) {
        this.custom_color = custom_color;
    }

    public int getUser_level() {
        return user_level;
    }

    public void setUser_level(int user_level) {
        this.user_level = user_level;
    }
}
