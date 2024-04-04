package com.example.beta;

import java.util.ArrayList;

public class User {
    private String uid;
    private String name;
    private String family;
    ArrayList<String> chores = new ArrayList<String>();

    public User(String uid, String name, String family){
        this.uid = uid;
        this.name = name;
        this.family = family;
    }
    public User(){

    }
    public String getUName() {
        return this.name;
    }
    public void setUName(String name) {
        this.name=name;
    }
    public String getUid() {
        return uid; }
    public void setUid(String uid) {
        this.uid=uid;
    }
    public void setFamily(String family){ this.family = family; }
    public String getFamily(){ return this.family; }
    public void addChore (String kid){
        this.chores.add(kid);
    }
    public ArrayList<String> getChores(){
        return this.chores;
    }
}