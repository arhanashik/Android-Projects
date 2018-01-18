package com.project.blackspider.quarrelchat.Model;

public class ChatReport {

    private int index;
    private int sl;
    private String from;
    private String to;
    private String myName;
    private int report;
    private String timestamp;
    private String myFcmId;
    private String targetFcmId;
    private boolean friend;
    private boolean available;
    private boolean online;
    private boolean typing;

    public ChatReport() {
    }

    public ChatReport(int sl, String from, String to, String myName, int report,
                      String timestamp, String myFcmId, String targetFcmId, boolean friend,
                      boolean available, boolean online, boolean typing) {
        this.sl = sl;
        this.from = from;
        this.to = to;
        this.myName = myName;
        this.report = report;
        this.timestamp = timestamp;
        this.myFcmId = myFcmId;
        this.targetFcmId = targetFcmId;
        this.friend = friend;
        this.available = available;
        this.online = online;
        this.typing = typing;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public int getReport() {
        return report;
    }

    public void setReport(int report) {
        this.report = report;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMyFcmId() {
        return myFcmId;
    }

    public void setMyFcmId(String myFcmId) {
        this.myFcmId = myFcmId;
    }

    public String getTargetFcmId() {
        return targetFcmId;
    }

    public void setTargetFcmId(String targetFcmId) {
        this.targetFcmId = targetFcmId;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }
}
