package com.example.beta;

public class Chore {
    private String key;
    private String title;
    private String description;
    private String dataImage;
    private String due;
    private String creator;
    private String whoEnded = "-1";
    private String timeEnd;


    public Chore(String title, String description,  String dataImage){
        this.title = title;
        this.description = description;
        this.dataImage = dataImage;
    }
    public  Chore(){

    }
    public String getDataImage() {
        return dataImage;
    }

    public void setKey(String key){
        this.key = key;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setWhoEnded(String whoEnded){
        this.whoEnded = whoEnded;
    }

    public String getKey(){
        return key;
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
    public String  getCreator(){
        return creator;
    }
    public String  getWhoEnded(){
        return whoEnded;
    }






}
