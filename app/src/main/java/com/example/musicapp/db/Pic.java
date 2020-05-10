package com.example.musicapp.db;

public class Pic {

    private String name;

    private int imageId;

    public Pic(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }
}
