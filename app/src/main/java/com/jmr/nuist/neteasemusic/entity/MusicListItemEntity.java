package com.jmr.nuist.neteasemusic.entity;

public class MusicListItemEntity {
    private String id; //歌曲id
    private String musicName; // 歌曲名称
    private String count; // 序号
    private String author; // 作者
    private String album; // 所属专辑
    private String alPicUrl; // 专辑图片

    public MusicListItemEntity(String id, String musicName, String author, String album, String count, String alPicUrl) {
        this.id = id;
        this.musicName = musicName;
        this.author = author;
        this.album = album;
        this.count = count;
        this.alPicUrl = alPicUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlPicUrl() {
        return alPicUrl;
    }

    public void setAlPicUrl(String alPicUrl) {
        this.alPicUrl = alPicUrl;
    }
}
