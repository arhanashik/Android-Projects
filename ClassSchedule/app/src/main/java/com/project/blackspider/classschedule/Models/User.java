package com.project.blackspider.classschedule.Models;

/**
 * Created by Mr blackSpider on 12/22/2016.
 */

public class User {
    private String name;
    private String ID;
    private String reg;
    private String faculty;
    private String session;
    private String phone;
    private String email;
    private String password;
    private String image_path;
    private String offline_image_path;
    private String date;
    private String fcmDeviceRegID;
    private String status;
    private String lastMessage;

    public User() {
    }

    public User(String name, String ID, String reg, String faculty, String session, String phone, String email, String password, String image_path, String fcmDeviceRegID, String status) {
        this.name = name;
        this.ID = ID;
        this.reg = reg;
        this.faculty = faculty;
        this.session = session;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.image_path = image_path;
        this.fcmDeviceRegID = fcmDeviceRegID;
        this.status = status;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage_path() {
        return image_path;
    }

    public String getOffline_image_path() {
        return offline_image_path;
    }

    public void setOffline_image_path(String offline_image_path) {
        this.offline_image_path = offline_image_path;
    }


    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFcmDeviceRegID() {
        return fcmDeviceRegID;
    }

    public void setFcmDeviceRegID(String fcmDeviceRegID) {
        this.fcmDeviceRegID = fcmDeviceRegID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
