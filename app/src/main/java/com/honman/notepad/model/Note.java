package com.honman.notepad.model;

public class Note {
    private long id;
    private String title;
    private String content;
    private long createTime;
    private long updateTime;

    public Note() {
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}