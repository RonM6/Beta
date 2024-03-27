package com.example.beta;

public class User {
    private String uid;
    private String name;
    private Family family;
    private Chore [] activeChores;

    public User(String uid, String name, Family family){
        this.uid = uid;
        this.name = name;
        this.family = family;
    }

    public String getUName() {
        return this.name;
    }
    public void setUName(String name) {
        this.name=name;
    }
    public String getUid() { return uid; }
    public void setUid(String uid) {
        this.uid=uid;
    }
}