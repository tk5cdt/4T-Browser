package com.example.a4tbrowser.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.a4tbrowser.model.Websites;

import java.util.ArrayList;
import java.util.List;

public class MyDbHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "4t_Browser.db";
    private static final String TABLE_NAME = "History";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_URL = "url";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TIMEE = "timee";

    public MyDbHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        String createTable = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_URL + " TEXT,"
                + COLUMN_TIMEE + " TEXT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_IMAGE + " BLOB"
                + ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addWebsite(Websites websites) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_URL, websites.getUrl());
            values.put(COLUMN_TIMEE, websites.getTime());
            values.put(COLUMN_TITLE, websites.getTitle());
            values.put(COLUMN_IMAGE, websites.getImage());

            db.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e("MyDbHandler", "Error inserting data", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    //Delete from database
    public void deleteWebsite(Websites websites) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_URL + " = ?", new String[]{websites.getUrl()});
        db.close();
    }

    // printout the history as string
    public List<String> databaseToString() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;

        List<String> dbstring = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int i = 0;
        if (cursor.moveToNext()) {
            do {
                if (cursor.getString(2) != null) {
                    dbstring.add(i, cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3));

                    i++;
                }

            } while (cursor.moveToNext());
            cursor.close();
        }
        return dbstring;
    }

    // get all urls from database
    public List<Websites> getAllUrls() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        List<Websites> websitesList = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        if (cursor.moveToNext()) {
            do {
                Websites websites = new Websites();
                websites.setUrl(cursor.getString(1));
                websites.setTime(cursor.getString(2));
                websites.setImage(cursor.getBlob(4));
                websites.setTitle(cursor.getString(3));
                websitesList.add(websites);
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
            return websitesList;
        } else {
            cursor.close();
            return null;
        }
    }
}



