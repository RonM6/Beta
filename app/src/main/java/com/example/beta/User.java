package com.example.beta;

import java.util.ArrayList;

public class User {
    ArrayList<String> chores = new ArrayList<String>();
    private String uid;
    private String name;
    private String family;

    public User(String uid, String name, String family) {
        this.uid = uid;
        this.name = name;
        this.family = family;
    }

    public User() {

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFamily() {
        return this.family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public void addChore(String cid) {
        this.chores.add(cid);
    }

    public ArrayList<String> getChores() {
        return this.chores;
    }

    public void setChores(ArrayList<String> chores) {
        this.chores = chores;
    }

}