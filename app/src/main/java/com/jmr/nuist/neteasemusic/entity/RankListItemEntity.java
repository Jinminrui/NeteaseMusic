package com.jmr.nuist.neteasemusic.entity;

public class RankListItemEntity {
    private String id;
    private String coverImgUrl;
    private String first;
    private String second;
    private String third;


    public RankListItemEntity(String id,String coverImgUrl, String first, String second, String third) {
        this.id = id;
        this.coverImgUrl = coverImgUrl;
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getThird() {
        return third;
    }

    public void setThird(String third) {
        this.third = third;
    }
}
