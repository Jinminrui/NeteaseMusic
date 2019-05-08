package com.jmr.nuist.neteasemusic.entity;

public class PlayListItemEntity {
    private String id;
    private String title;
    private String imageUrl;
    private String desc;
    private String updateTime;


    public PlayListItemEntity(String id, String title, String imageUrl, String desc, String updateTime) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.desc = desc;
        this.updateTime = updateTime;
    }

    public PlayListItemEntity(String id, String title, String imageUrl, String desc) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
