package com.example.a4tbrowser.model;


import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.ByteArrayInputStream;

@Entity(tableName = "bookmark_table")
public class BookmarkEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "uid")
    private int uid;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "url_path")
    private String urlPath;

    @ColumnInfo(name = "img")
    private byte[] img;


    public BookmarkEntity()
    {

    }

    public BookmarkEntity(@NonNull String name, @NonNull String urlPath, byte[] img) {
        this.name = name;
        this.urlPath = urlPath;
        this.img = img;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(@NonNull String urlPath) {
        this.urlPath = urlPath;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }
}
