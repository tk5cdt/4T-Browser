package com.example.a4tbrowser.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.a4tbrowser.model.BookmarkEntity;

import java.util.List;

@Dao
public interface BookmarkDAO {
    @Insert
    void insertBookmark(BookmarkEntity b);

    @Query("select * from bookmark_table")
    List<BookmarkEntity> getListBookmark();


}
