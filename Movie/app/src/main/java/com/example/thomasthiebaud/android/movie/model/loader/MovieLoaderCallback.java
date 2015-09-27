package com.example.thomasthiebaud.android.movie.model.loader;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.thomasthiebaud.android.movie.model.contract.DatabaseContract;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thiebaudthomas on 24/09/15.
 */
public class MovieLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int ALL_MOVIE_LOADER = 0;
    public static final int ONE_MOVIE_LOADER = 1;

    private Activity activity;
    private LoaderResponse<MovieItem> response;
    private int movieId;

    public MovieLoaderCallback(Activity activity) {
        this.activity = activity;
    }

    public MovieLoaderCallback onResponse(LoaderResponse<MovieItem> response) {
        this.response = response;
        return this;
    }

    public MovieLoaderCallback setMovieId(int movieId) {
        this.movieId = movieId;
        return this;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case ALL_MOVIE_LOADER:
                cursorLoader =  new CursorLoader(activity,
                        DatabaseContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                break;
            case ONE_MOVIE_LOADER:
                cursorLoader = new CursorLoader(activity,
                    DatabaseContract.MovieEntry.CONTENT_URI,
                    null,
                    DatabaseContract.MovieEntry._ID + "= ?",
                    new String[]{movieId +""},
                    null);
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        response.onSuccess(cursorToMovies(cursor));
        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private List<MovieItem> cursorToMovies(Cursor cursor) {
        List<MovieItem> movies = new ArrayList<>();
        while (cursor.moveToNext()) {
            MovieItem item = new MovieItem();
            item.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.MovieEntry._ID)));
            item.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieEntry.COLUMN_TITLE)));
            item.setPosterPath(cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieEntry.COLUMN_POSTER_PATH)));
            item.setOverview(cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieEntry.COLUMN_OVERVIEW)));
            item.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(DatabaseContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
            item.setReleaseDate(cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieEntry.COLUMN_RELEASE_DATE)));
            movies.add(item);
        }
        return movies;
    }
}
