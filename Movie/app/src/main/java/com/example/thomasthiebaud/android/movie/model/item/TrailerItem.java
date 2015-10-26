package com.example.thomasthiebaud.android.movie.model.item;

import android.content.ContentValues;

import com.example.thomasthiebaud.android.movie.contract.DatabaseContract;

/**
 * Created by thiebaudthomas on 21/09/15.
 */
public class TrailerItem {
    private String id;
    private String name;
    private String key;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "TrailerItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", key='" + key + '\'' +
                '}';
    }

    public ContentValues toContentValues(int movieId) {
        ContentValues value = new ContentValues();
        value.put(DatabaseContract.TrailerEntry._ID, id);
        value.put(DatabaseContract.TrailerEntry.COLUMN_NAME, name);
        value.put(DatabaseContract.TrailerEntry.COLUMN_KEY, key);
        value.put(DatabaseContract.TrailerEntry.COLUMN_ID_MOVIE, movieId);
        return value;
    }
}
