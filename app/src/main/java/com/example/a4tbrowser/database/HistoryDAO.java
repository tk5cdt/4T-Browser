package com.example.a4tbrowser.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.a4tbrowser.model.Websites;

import java.util.List;

@Dao
public interface HistoryDAO {
    @Insert
    void addHistory(Websites websites);

    @Query("SELECT * FROM websites")
    List<Websites> getAllHistory();
    @Query("DELETE FROM websites")
    void deleteAll();
    @Query("DELETE FROM websites WHERE url = :url and title = :title and timee = :timee")
    void delete(String url, String title, String timee);

    @Query("DELETE FROM websites WHERE url = :url and title = :title")
    void delete2(String url, String title);

    @Query("SELECT * FROM websites WHERE date = :date")
    List<Websites> getHistoryByDate(String date);


}
