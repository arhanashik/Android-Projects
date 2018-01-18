package com.project.blackspider.classschedule.Models;

/**
 * Created by Mr blackSpider on 7/29/2017.
 */

public class ScheduleItem {
    private int id;
    private String sl, name, email, courseCode, CourseTitle, courseTeacher,
            classRoom, classTime, description, timestamp, url, profileImage, postImage;

    public ScheduleItem() {
    }

    public ScheduleItem(String sl, String name, String email, String courseCode, String courseTitle,
                        String courseTeacher, String classRoom, String classTime, String description,
                        String timestamp, String url, String profileImage, String postImage) {
        this.sl = sl;
        this.name = name;
        this.email = email;
        this.courseCode = courseCode;
        CourseTitle = courseTitle;
        this.courseTeacher = courseTeacher;
        this.classRoom = classRoom;
        this.classTime = classTime;
        this.description = description;
        this.timestamp = timestamp;
        this.url = url;
        this.profileImage = profileImage;
        this.postImage = postImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSl() {
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return CourseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        CourseTitle = courseTitle;
    }

    public String getCourseTeacher() {
        return courseTeacher;
    }

    public void setCourseTeacher(String courseTeacher) {
        this.courseTeacher = courseTeacher;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }
}
