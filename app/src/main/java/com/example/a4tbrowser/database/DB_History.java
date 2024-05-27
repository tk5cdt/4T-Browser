package com.example.a4tbrowser.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.a4tbrowser.model.Websites;

@Database(entities = {Websites.class}, version = 1, exportSchema = true)
public abstract class DB_History extends RoomDatabase {
    private static final String DATABASE_NAME = "4t_Browser_2";

    public abstract HistoryDAO historyDAO();

    private static DB_History history; // instance

    public static DB_History getDatabase(Context context) {
        if (history == null) {
            synchronized (DB_History.class) {
                if (history == null) {
                    history = Room.databaseBuilder(context.getApplicationContext(),
                                    DB_History.class, DATABASE_NAME)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return history;
    }

}
