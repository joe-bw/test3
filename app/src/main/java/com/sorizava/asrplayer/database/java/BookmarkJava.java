/*
 * Create by jhong on 2022. 8. 8.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.database.java;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/** 바로가기 Room DB Entity */
@Entity(tableName = "bookmarks")
public class BookmarkJava {

    @PrimaryKey(autoGenerate = true)
    private int bookmarkId;

    private int position;

    private String imgName;

    private String imgUrl;

    private String name;

    private String url;

    public BookmarkJava(int bookmarkId, int position, String imgName, String imgUrl, String name, String url) {
        this.bookmarkId = bookmarkId;
        this.position = position;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
        this.name = name;
        this.url = url;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getBookmarkId() {
        return bookmarkId;
    }

    public void setBookmarkId(int bookmarkId) {
        this.bookmarkId = bookmarkId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
