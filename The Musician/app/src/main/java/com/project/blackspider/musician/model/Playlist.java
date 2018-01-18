package com.project.blackspider.musician.model;

/**
 * Created by Mr blackSpider on 9/8/2017.
 */

public class Playlist {
    int sl;
    String name;
    String cover;

    public Playlist() {
    }

    public Playlist(int sl, String name, String cover) {
        this.sl = sl;
        this.name = name;
        this.cover = cover;
    }

    public int getSl() {
        return sl;
    }

    public void setSl(int sl) {
        this.sl = sl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
