package com.example.a4tbrowser.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.a4tbrowser.model.BookmarkEntity;

@Database(entities = {BookmarkEntity.class}, version = 1, exportSchema = true)
public abstract class MyDatabase extends RoomDatabase{
    private static final String DATABSE_NAME = "4t_Browser";

    public abstract BookmarkDAO bDAO();

    private static MyDatabase mDB; // instance

    public static MyDatabase getDatabase(Context context)
    {
        if(mDB == null)
        {
            synchronized (MyDatabase.class)
            {
                if(mDB == null)
                {
                    mDB = Room.databaseBuilder(context.getApplicationContext(),
                            MyDatabase.class, DATABSE_NAME)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return  mDB;
    }


}
