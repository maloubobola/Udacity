package com.example.thomasthiebaud.android.movie.model.loader;

import android.app.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.thomasthiebaud.android.movie.adapter.TrailerAdapter;
import com.example.thomasthiebaud.android.movie.model.contract.DatabaseContract;
import com.example.thomasthiebaud.android.movie.model.item.TrailerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thiebaudthomas on 24/09/15.
 */
public class TrailerLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int TRAILER_LOADER_ID = 200;

    private Activity activity;
    private TrailerAdapter adapter;
    private int movieId;

    public TrailerLoaderCallback(Activity activity, TrailerAdapter adapter, int movieId) {
        this.activity = activity;
        this.adapter = adapter;
        this.movieId = movieId;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case TRAILER_LOADER_ID:
                cursorLoader = new CursorLoader(activity,
                        DatabaseContract.TrailerEntry.CONTENT_URI,
                        null,
                        DatabaseContract.TrailerEntry.COLUMN_ID_MOVIE + "= ?",
                        new String[]{movieId + ""},
                        null);
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.clear();
        adapter.addAll(cursorToTrailers(cursor));
        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private List<TrailerItem> cursorToTrailers(Cursor cursor) {
        List<TrailerItem> trailers = new ArrayList<>();
        while (cursor.moveToNext()) {
            TrailerItem item = new TrailerItem();
            item.setId(cursor.getString(cursor.getColumnIndex(DatabaseContract.TrailerEntry._ID)));
            item.setKey(cursor.getString(cursor.getColumnIndex(DatabaseContract.TrailerEntry.COLUMN_KEY)));
            item.setName(cursor.getString(cursor.getColumnIndex(DatabaseContract.TrailerEntry.COLUMN_NAME)));
            trailers.add(item);
        }
        return trailers;
    }
}
