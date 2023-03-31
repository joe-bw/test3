/*
 * Create by jhong on 2022. 8. 8.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main.bookmark;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sorizava.asrplayer.data.item.BookmarkItem;
import com.sorizava.asrplayer.database.java.BookmarkJava;
import com.sorizava.asrplayer.database.java.BookmarkRepositoryJava;

import java.util.ArrayList;
import java.util.List;

public class BookmarkViewModel extends AndroidViewModel {

    public void setEditMode(Boolean mode) {
        editMode.setValue(mode);
    }

    public LiveData<Boolean> isEditMode() {
        return editMode;
    }

    private MutableLiveData<Boolean> editMode = new MutableLiveData<>(false);

    private BookmarkRepositoryJava repository;

    LiveData<List<BookmarkJava>> bookmarks;

    public MutableLiveData<List<BookmarkJava>> getDbBookmarks() {
        return dbBookmarks;
    }

    public final MutableLiveData<List<BookmarkJava>> dbBookmarks = new MutableLiveData<>();

    public BookmarkViewModel(@NonNull Application application) {
        super(application);
        repository = new BookmarkRepositoryJava(application);
        bookmarks = repository.getBookmarks();
    }

    void requestBeAddedBookmarkList(List<BookmarkItem> bookmarks) {
        dbBookmarks.setValue(convertItemToJava((ArrayList<BookmarkItem>) bookmarks));
    }

    void changeItems(ArrayList<BookmarkItem> bookmarks) {
        repository.changeAllItem(convertItemToJava(bookmarks));
    }

    private ArrayList<BookmarkJava> convertItemToJava(ArrayList<BookmarkItem> bookmarks) {

        int position = 0;

        ArrayList<BookmarkJava> list = new ArrayList<>();
        for (BookmarkItem item: bookmarks){
            list.add(new BookmarkJava(item.getBookmarkId(), position, item.getImgName(), item.getImgUrl(), item.getName(), item.getUrl()));
            position++;
        }

        list.remove(list.size() - 1);

        return list;

    }
}
