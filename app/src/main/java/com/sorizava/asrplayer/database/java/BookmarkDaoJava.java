/*
 * Create by jhong on 2022. 8. 8.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.database.java;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/** 바로가기 Room Dao 클래스 */
@Dao
public interface BookmarkDaoJava {

    @Query("SELECT * FROM bookmarks ORDER BY position")
    List<BookmarkJava> getBookmarkList();

    @Query("SELECT * FROM bookmarks ORDER BY position")
    LiveData<List<BookmarkJava>> getBookmarks();

    @Query("SELECT * FROM bookmarks WHERE bookmarkId = :bookmarkId")
    LiveData<BookmarkJava> getBookmark(int bookmarkId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BookmarkJava> bookmarks);

    @Insert
    void insertBookmark(BookmarkJava bookmark);

    @Update
    void updateBookmark(BookmarkJava bookmark);

    @Delete
    void deleteBookmark(BookmarkJava bookmark);

    @Query("DELETE FROM bookmarks")
    void deleteAll();
}
