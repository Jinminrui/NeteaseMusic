package com.jmr.nuist.neteasemusic.entity;

public class MyPlaylistItemEntity {
    private String id;
    private String name;
    private String imageUrl;
    private String songNums;
    private String creator;
    private String playCount;


    public MyPlaylistItemEntity(String id, String name, String imageUrl, String songNums, String creator, String playCount) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.songNums = songNums;
        this.creator = creator;
        this.playCount = playCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSongNums() {
        return songNums;
    }

    public void setSongNums(String songNums) {
        this.songNums = songNums;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }
}
