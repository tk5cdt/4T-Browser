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
//                            .addMigrations(MIGRATION_1_2)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return history;
    }

//    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            // Rename the existing table
////            database.execSQL("ALTER TABLE Websites RENAME TO Websites_old");
//
//            // Create the new table with the correct schema
//            database.execSQL("CREATE TABLE Websites (" +
//                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
//                    "url TEXT NOT NULL, " +
//                    "image BLOB, " +
//                    "title TEXT NOT NULL, " +
//                    "timee TEXT NOT NULL, " +
//                    "date TEXT NOT NULL )");
//
//            // Copy the data from the old table to the new table
////            database.execSQL("INSERT INTO Websites (id, url, image, title, timee, date)");
////                    "SELECT id, url, '', '', image, title FROM Websites_old");
//
//            // Remove the old table
////            database.execSQL("DROP TABLE Websites_old");
//        }
//    };
}
