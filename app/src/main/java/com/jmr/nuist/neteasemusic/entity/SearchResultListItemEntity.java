package com.jmr.nuist.neteasemusic.entity;

public class SearchResultListItemEntity {
    private String songId;
    private String songName;
    private String artists;


    public SearchResultListItemEntity(String songId, String songName, String artists) {
        this.songId = songId;
        this.songName = songName;
        this.artists = artists;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }
}
