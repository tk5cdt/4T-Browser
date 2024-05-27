package com.example.a4tbrowser.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "websites")
public class Websites {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    private String url;
    private byte[] image;
    @NonNull
    private String title;
    @NonNull
    private String timee;
    @NonNull
    private String date;



    public Websites()
    {

    }

    public Websites(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public Websites(@NonNull String url, byte[] image, @NonNull String title, @NonNull String timee, @NonNull String date) {
        this.url = url;
        this.image = image;
        this.title = title;
        this.timee = timee;
        this.date = date;
    }

    public Websites(String url, String timee, String title) {
        this.url = url;
        this.timee = timee;
        this.title = title;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getTimee() {
        return timee;
    }

    public void setTimee(@NonNull String timee) {
        this.timee = timee;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }
}
