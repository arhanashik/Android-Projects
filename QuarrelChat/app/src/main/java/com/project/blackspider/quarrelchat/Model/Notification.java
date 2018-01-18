package com.project.blackspider.quarrelchat.Model;

/**
 * Created by Mr blackSpider on 8/13/2017.
 */

public class Notification {
    private int sl;
    private String from;
    private String to;
    private String notification;
    private String soulmateName;
    private String myName;
    private String imagePath;
    private String status;
    private String timestamp;
    private int type;
    private boolean  fromMe;

    public Notification() {
    }

    public Notification(int sl, String from, String to, String notification, String imagePath, String status, String timestamp, int type) {
        this.sl = sl;
        this.from = from;
        this.to = to;
        this.notification = notification;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
        this.type = type;
    }

    public int getSl() {
        return sl;
    }

    public void setSl(int sl) {
        this.sl = sl;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getSoulmateName() {
        return soulmateName;
    }

    public void setSoulmateName(String soulmateName) {
        this.soulmateName = soulmateName;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public void setFromMe(boolean fromMe) {
        this.fromMe = fromMe;
    }
}
