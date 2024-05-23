package com.example.beta;

public class Chore {
    private String key;
    private String cid;
    private String title;
    private String description;
    private String dataImage;
    private String dueDate;
    private String dueTime;
    private String creator;
    private String whoEnded = "-1";
    private String timeEnd;
    private String status;


    public Chore(String title, String description, String dataImage) {
        this.title = title;
        this.description = description;
        this.dataImage = dataImage;
    }

    public Chore(String title, String description, String dataImage, String creator, String status, String cid) {
        this.title = title;
        this.description = description;
        this.dataImage = dataImage;
        this.creator = creator;
        this.status = status;
        this.cid = cid;
    }

    public Chore() {

    }

    public String getDataImage() {
        return dataImage;
    }

    public void setDataImage(String dataImage) {
        this.dataImage = dataImage;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getWhoEnded() {
        return whoEnded;
    }

    public void setWhoEnded(String whoEnded) {
        this.whoEnded = whoEnded;
    }

    public String getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return this.dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }
}
