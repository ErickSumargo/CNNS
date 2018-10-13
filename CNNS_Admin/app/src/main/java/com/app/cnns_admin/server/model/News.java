package com.app.cnns_admin.server.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class News {
    private int id;
    private String title, content, image, video, date;
    private double latitude, longitude;

    private int ups, downs, views;
    private int status;

    @SerializedName("created_at")
    private String createdAt;

    private User user;

    private List<Comment> comments;

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }


    public String getImage() {
        return image;
    }


    public void setImage(String image) {
        this.image = image;
    }


    public String getVideo() {
        return video;
    }


    public void setVideo(String video) {
        this.video = video;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public int getUps() {
        return ups;
    }


    public void setUps(int ups) {
        this.ups = ups;
    }

    public int getDowns() {
        return downs;
    }


    public void setDowns(int downs) {
        this.downs = downs;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public List<Comment> getComments() {
        return comments;
    }


    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}