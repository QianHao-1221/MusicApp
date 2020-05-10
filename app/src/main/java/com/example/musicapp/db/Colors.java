package com.example.musicapp.db;

public class Colors {

    private String name;

    private int imageId;

    public Colors(String name, int imageId) {
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
