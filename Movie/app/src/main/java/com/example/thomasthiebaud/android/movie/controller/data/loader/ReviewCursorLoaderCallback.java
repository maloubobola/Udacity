package com.example.thomasthiebaud.android.movie.controller.data.loader;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

import com.example.thomasthiebaud.android.movie.model.contract.DatabaseContract;

/**
 * Created by thomasthiebaud on 04/10/15.
 */
public class ReviewCursorLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int ALL_REVIEW_LOADER = 200;

    private Activity activity;
    private CursorAdapter adapter;
    private int movieId;
    private String sortBy = "";

    public ReviewCursorLoaderCallback(Activity activity, CursorAdapter adapter, int movieId, String sortBy) {
        this.activity = activity;
        this.adapter = adapter;
        this.movieId = movieId;
        this.sortBy = sortBy;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case ALL_REVIEW_LOADER:
                cursorLoader = new CursorLoader(activity,
                        DatabaseContract.ReviewEntry.CONTENT_URI.buildUpon().appendPath(sortBy).appendPath(movieId+"").build(),
                        null,
                        DatabaseContract.ReviewEntry.COLUMN_ID_MOVIE + "= ?",
                        new String[]{movieId + ""},
                        null);
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()) {
            while (data.moveToNext()) {
                adapter.swapCursor(data);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
