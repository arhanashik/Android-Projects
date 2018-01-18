package com.project.blackspider.classschedule.Models;

/**
 * Created by Mr blackSpider on 12/29/2016.
 */

public class Teacher {
    private String name;
    private String designation;
    private String department;
    private String faculty;
    private String phone;
    private String email;
    private String password;
    private String date;
    private String fcmDeviceRegID;
    private String status;

    public Teacher() {
    }

    public Teacher(String name, String designation, String department, String faculty, String phone, String email, String password, String fcmDeviceRegID, String status) {
        this.name = name;
        this.designation = designation;
        this.department = department;
        this.faculty = faculty;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.fcmDeviceRegID = fcmDeviceRegID;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
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
}
