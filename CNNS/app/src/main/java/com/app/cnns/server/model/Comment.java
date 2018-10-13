package com.app.cnns.server.model;

import com.google.gson.annotations.SerializedName;

public class Comment {
    private int id;
    private String content;

    private int like, ups, downs;

    @SerializedName("created_at")
    private String createdAt;

    private User user;

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public int getLike() {
        return like;
    }


    public void setLike(int like) {
        this.like = like;
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


    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public String getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
