package com.example.thomasthiebaud.android.movie.model.loader;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.thomasthiebaud.android.movie.adapter.ReviewAdapter;
import com.example.thomasthiebaud.android.movie.model.contract.DatabaseContract;
import com.example.thomasthiebaud.android.movie.model.item.ReviewItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thiebaudthomas on 24/09/15.
 */
public class ReviewLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int REVIEW_LOADER_ID = 100;

    private Activity activity;
    private ReviewAdapter adapter;
    private int movieId;

    public ReviewLoaderCallback(Activity activity, ReviewAdapter adapter, int movieId) {
        this.activity = activity;
        this.adapter = adapter;
        this.movieId = movieId;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case REVIEW_LOADER_ID:
                cursorLoader = new CursorLoader(activity,
                        DatabaseContract.ReviewEntry.CONTENT_URI,
                        null,
                        DatabaseContract.ReviewEntry.COLUMN_ID_MOVIE + "= ?",
                        new String[]{movieId + ""},
                        null);
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.clear();
        adapter.addAll(cursorToReviews(cursor));
        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private List<ReviewItem> cursorToReviews(Cursor cursor) {
        List<ReviewItem> reviews = new ArrayList<>();
        while (cursor.moveToNext()) {
            ReviewItem item = new ReviewItem();
            item.setId(cursor.getString(cursor.getColumnIndex(DatabaseContract.ReviewEntry._ID)));
            item.setAuthor(cursor.getString(cursor.getColumnIndex(DatabaseContract.ReviewEntry.COLUMN_AUTHOR)));
            item.setContent(cursor.getString(cursor.getColumnIndex(DatabaseContract.ReviewEntry.COLUMN_CONTENT)));
            item.setUrl(cursor.getString(cursor.getColumnIndex(DatabaseContract.ReviewEntry.COLUMN_URL)));
            reviews.add(item);
        }
        return reviews;
    }
}
