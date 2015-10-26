package com.example.thomasthiebaud.android.movie.model.item;

import android.content.ContentValues;

import com.example.thomasthiebaud.android.movie.contract.DatabaseContract;

/**
 * Created by thiebaudthomas on 21/09/15.
 */
public class ReviewItem {
    private String id;
    private String author;
    private String content;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ReviewItem{" +
                "id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public ContentValues toContentValues(int movieId) {
        ContentValues value = new ContentValues();
        value.put(DatabaseContract.ReviewEntry._ID, id);
        value.put(DatabaseContract.ReviewEntry.COLUMN_AUTHOR, author);
        value.put(DatabaseContract.ReviewEntry.COLUMN_CONTENT, content);
        value.put(DatabaseContract.ReviewEntry.COLUMN_URL, url);
        value.put(DatabaseContract.ReviewEntry.COLUMN_ID_MOVIE, movieId);
        return value;
    }
}
