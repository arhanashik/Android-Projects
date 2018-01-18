package com.project.blackspider.classschedule.Models;

/**
 * Created by Mr blackSpider on 1/7/2017.
 */

public class Message {
    private String sl;
    private String message;
    private String sent;
    private String delivered;
    private String seen;
    private String date;

    public Message() {
    }

    public Message(String message, String sent, String delivered, String seen, String date) {
        this.message = message;
        this.sent = sent;
        this.delivered = delivered;
        this.seen = seen;
        this.date = date;
    }

    public String getSl() {
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public String getDelivered() {
        return delivered;
    }

    public void setDelivered(String delivered) {
        this.delivered = delivered;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
