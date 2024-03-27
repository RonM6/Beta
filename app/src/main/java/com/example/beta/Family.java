package com.example.beta;

public class Family {
    private String fid;
    private String name;
    private User [] users;

    public Family(String name, String fid){
        this.name = name;
        this.fid = fid;
    }
    public String getFName() {
        return this.name;
    }
    public void setFName(String name) {
        this.name=name;
    }
    public String getFid() { return fid; }
    public void setFid(String fid) {
        this.fid=fid;
    }

}
