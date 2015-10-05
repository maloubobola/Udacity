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
 * Created by thomasthiebaud on 03/10/15.
 */
public class MovieCursorLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int ALL_MOVIE_LOADER = 100;
    public static final int ONE_MOVIE_LOADER = 101;

    private Activity activity;
    private CursorAdapter adapter;
    private String sortBy = "";

    public MovieCursorLoaderCallback(Activity activity, CursorAdapter adapter, String sortBy) {
        this.activity = activity;
        this.adapter = adapter;
        this.sortBy = sortBy;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case ALL_MOVIE_LOADER:
                cursorLoader =  new CursorLoader(activity,
                        DatabaseContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(sortBy).build(),
                        null,
                        null,
                        null,
                        null);
                break;
            case ONE_MOVIE_LOADER:
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
        if(data.moveToFirst())
            while (data.moveToNext())
                adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
