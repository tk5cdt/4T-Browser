package com.example.a4tbrowser.model;

public class Websites {
    private int id;
    private String url;
    private byte[] image;
    private String title;
    private String timee;


    public Websites()
    {

    }

    public Websites(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public Websites(String url, String time, byte[] image, String title) {
        this.url = url;
        this.image = image;
        this.title = title;
        this.timee = time;
    }


    public Websites(String url, String timee, String title) {
        this.url = url;
        this.timee = timee;
        this.title = title;

    }

    public Websites(String url) {
        this.url = url;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return timee;
    }

    public void setTime(String time) {
        this.timee = time;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getTimee() {
        return timee;
    }

    public void setTimee(String timee) {
        this.timee = timee;
    }
}
