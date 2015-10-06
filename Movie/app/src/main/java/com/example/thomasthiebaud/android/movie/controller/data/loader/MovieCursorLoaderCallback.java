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
    private String sortBy;
    private int movieId;
    private OnResponseCallback<Boolean> isFavoriteCallback;

    public MovieCursorLoaderCallback(Activity activity) {
        this.activity = activity;
    }

    public MovieCursorLoaderCallback setAdapter(CursorAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public MovieCursorLoaderCallback setSortOrder(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public MovieCursorLoaderCallback setIsFavoriteCallback(OnResponseCallback<Boolean> isFavoriteCallback) {
        this.isFavoriteCallback = isFavoriteCallback;
        return this;
    }

    public MovieCursorLoaderCallback setMovieId(int movieId) {
        this.movieId = movieId;
        return this;
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
        if(isFavoriteCallback != null) {
            boolean isFavorite = false;
            if(data.moveToFirst())
                isFavorite = true;
            isFavoriteCallback.onResponse(new Boolean(isFavorite));
        }

        if(adapter != null) {
            if(data.moveToFirst())
                while (data.moveToNext())
                    adapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
