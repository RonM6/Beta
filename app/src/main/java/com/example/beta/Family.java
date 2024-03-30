package com.example.beta;

import java.util.ArrayList;

public class Family {
    private String fid;
    private String name;
    private ArrayList<String> users = new ArrayList<>();

    public Family(String name){
        this.name = name;
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
    public void addUser(String uid){
        this.users.add(uid);
    }

}
