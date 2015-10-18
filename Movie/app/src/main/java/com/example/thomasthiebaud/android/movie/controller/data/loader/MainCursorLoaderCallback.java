package com.example.thomasthiebaud.android.movie.controller.data.loader;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;

import com.example.thomasthiebaud.android.movie.model.contract.DatabaseContract;
import com.example.thomasthiebaud.android.movie.model.contract.LoaderContract;

/**
 * Created by thomasthiebaud on 03/10/15.
 */
public class MainCursorLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainCursorLoaderCallback.class.getSimpleName();

    private Activity activity;
    private CursorAdapter adapter;
    private String sortBy;

    public MainCursorLoaderCallback(Activity activity) {
        this.activity = activity;
    }

    public MainCursorLoaderCallback setAdapter(CursorAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public MainCursorLoaderCallback setSortOrder(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case LoaderContract.ALL_MOVIE_LOADER:
                cursorLoader =  new CursorLoader(activity,
                        DatabaseContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(sortBy).build(),
                        null,
                        null,
                        null,
                        null);
                break;
            case LoaderContract.ONE_MOVIE_LOADER:
                cursorLoader =  new CursorLoader(activity,
                        DatabaseContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(sortBy).build(),
                        null,
                        null,
                        null,
                        null);
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(adapter != null && data != null)
            adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
