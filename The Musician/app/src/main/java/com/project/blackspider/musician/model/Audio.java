package com.project.blackspider.musician.model;

import java.io.Serializable;

/**
 * Created by Mr blackSpider on 9/4/2017.
 */

public class Audio implements Serializable {

    private int id;
    private int albumId;
    private String data;
    private String title;
    private String album;
    private String artist;
    private long duration;

    public Audio(int id, int albumId, String data, String title, String album, String artist, long duration) {
        this.id = id;
        this.albumId = albumId;
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
