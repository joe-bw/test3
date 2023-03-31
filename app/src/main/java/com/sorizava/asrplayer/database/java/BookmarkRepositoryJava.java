/*
 * Create by jhong on 2022. 8. 8.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.database.java;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
/** 바로가기 Query 클래스 */
public class BookmarkRepositoryJava {

    private BookmarkDaoJava dao;

    public BookmarkRepositoryJava(Application application){
        AppDatabaseJava db = AppDatabaseJava.getDatabase(application);
        dao = db.bookmarkDao();
    }

    public LiveData<List<BookmarkJava>> getBookmarks() {
        return dao.getBookmarks();
    }

    public LiveData<BookmarkJava> getBookmark(int bookmarkId) {
        return dao.getBookmark(bookmarkId);
    }

    public void insertBookmark(BookmarkJava bookmark) {
        AppDatabaseJava.databaseWriteExecutor.execute(() -> {

            List<BookmarkJava> list = dao.getBookmarkList();

            int id = 0;

            for (BookmarkJava bookmarkJava : list) {
                if (id <= bookmarkJava.getBookmarkId()) {
                    id = bookmarkJava.getBookmarkId() + 1;
                }
            }

            int size = list.size();

            bookmark.setBookmarkId(id);
            bookmark.setPosition(size);
            dao.insertBookmark(bookmark);
        });
    }
    public void deleteBookmark(BookmarkJava bookmark) {
        dao.deleteBookmark(bookmark);
    }

    public void changeAllItem(ArrayList<BookmarkJava> bookmarks) {
        AppDatabaseJava.databaseWriteExecutor.execute(() -> {
            dao.deleteAll();
            dao.insertAll(bookmarks);
        });
    }
}
