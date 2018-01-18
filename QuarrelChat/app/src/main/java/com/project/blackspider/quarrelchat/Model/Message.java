package com.project.blackspider.quarrelchat.Model;

public class Message {

    private int index;
    private int sl;
    private String from;
    private String to;
    private String myName;
    private String message;
    private String timestamp;
    private String imagePath;
    private String myFcmId;
    private String targetFcmId;
    private int report;
    private boolean same;
    private int totalSentMsg;
    private int totalReceivedMsg;
    private int totalMsgCount;

    private int type;

    public Message() {
    }

    public Message(int sl, String from, String to, String message, String timestamp, String imagePath, int report, int type) {
        this.sl = sl;
        this.from = from;
        this.to = to;
        this.message = message;
        this.timestamp = timestamp;
        this.imagePath = imagePath;
        this.report = report;
        this.type = type;
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

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    public int getReport() {
        return report;
    }

    public void setReport(int report) {
        this.report = report;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isSame() {
        return same;
    }

    public void setSame(boolean same) {
        this.same = same;
    }

    public int getTotalSentMsg() {
        return totalSentMsg;
    }

    public void setTotalSentMsg(int totalSentMsg) {
        this.totalSentMsg = totalSentMsg;
    }

    public int getTotalReceivedMsg() {
        return totalReceivedMsg;
    }

    public void setTotalReceivedMsg(int totalReceivedMsg) {
        this.totalReceivedMsg = totalReceivedMsg;
    }

    public int getTotalMsgCount() {
        return totalMsgCount;
    }

    public void setTotalMsgCount(int totalMsgCount) {
        this.totalMsgCount = totalMsgCount;
    }
}
