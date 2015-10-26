package com.example.thomasthiebaud.android.movie.app.details;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;

import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.model.adapter.ReviewAdapter;
import com.example.thomasthiebaud.android.movie.model.adapter.TrailerAdapter;
import com.example.thomasthiebaud.android.movie.contract.BundleContract;
import com.example.thomasthiebaud.android.movie.contract.DatabaseContract;
import com.example.thomasthiebaud.android.movie.contract.LoaderContract;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;

import java.util.List;

/**
 * Created by thomasthiebaud on 17/10/15.
 */
public class DetailCursorLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = DetailCursorLoaderCallback.class.getSimpleName();

    private Activity activity;
    private boolean isFavorite = false;
    private String movieId = "";
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private MovieItem movieItem;
    private DetailFragment detailFragment;

    public DetailCursorLoaderCallback(DetailFragment detailFragment) {
        this.detailFragment = detailFragment;
        this.activity = detailFragment.getActivity();
        this.reviewAdapter = detailFragment.getReviewAdapter();
        this.trailerAdapter = detailFragment.getTrailerAdapter();
        this.movieItem = detailFragment.getItem();
        this.movieId = movieItem.getId() + "";
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case LoaderContract.ONE_MOVIE_LOADER:
                cursorLoader = new CursorLoader(activity,
                        DatabaseContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieId + "").build(),
                        null,
                        DatabaseContract.ReviewEntry._ID + "= ?",
                        new String[]{movieId},
                        null);
                break;
            case LoaderContract.ALL_REVIEW_LOADER:
                cursorLoader = new CursorLoader(activity,
                        DatabaseContract.ReviewEntry.CONTENT_URI.buildUpon().appendPath(args.getString(BundleContract.SORT_BY)).appendPath(movieId+"").build(),
                        null,
                        DatabaseContract.ReviewEntry.COLUMN_ID_MOVIE + "= ?",
                        new String[]{movieId},
                        null);
                break;
            case LoaderContract.ALL_TRAILER_LOADER:
                cursorLoader = new CursorLoader(activity,
                        DatabaseContract.TrailerEntry.CONTENT_URI.buildUpon().appendPath(args.getString(BundleContract.SORT_BY)).appendPath(movieId+"").build(),
                        null,
                        DatabaseContract.TrailerEntry.COLUMN_ID_MOVIE + "= ?",
                        new String[]{movieId},
                        null);
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LoaderContract.ONE_MOVIE_LOADER:
                if(data != null && data.moveToFirst()) {
                    ((FloatingActionButton) activity.findViewById(R.id.favorite_button)).setImageResource(R.drawable.ic_favorite_black);
                    isFavorite = true;
                }
                else {
                    ((FloatingActionButton) activity.findViewById(R.id.favorite_button)).setImageResource(R.drawable.ic_favorite_border_black);
                    isFavorite = false;
                }
                break;
            case LoaderContract.ALL_REVIEW_LOADER:
                if(data != null) {
                    while (data.moveToNext()) {
                        reviewAdapter.swapCursor(data);
                    }
                }
                break;
            case LoaderContract.ALL_TRAILER_LOADER:
                if(data != null) {
                    if(data.moveToFirst()) {
                        detailFragment.createShareActionProvider(data.getString(DatabaseContract.TrailerEntry.COLUMN_KEY_INDEX));
                        trailerAdapter.swapCursor(data);
                    }
                    while (data.moveToNext())
                        trailerAdapter.swapCursor(data);
                }
                break;
        }

        activity.findViewById(R.id.favorite_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    activity.getContentResolver().delete(
                            DatabaseContract.ReviewEntry.CONTENT_URI,
                            DatabaseContract.ReviewEntry.COLUMN_ID_MOVIE + " = ?",
                            new String[]{movieId + ""});
                    activity.getContentResolver().delete(
                            DatabaseContract.TrailerEntry.CONTENT_URI,
                            DatabaseContract.TrailerEntry.COLUMN_ID_MOVIE + " = ?",
                            new String[]{movieId + ""});
                    activity.getContentResolver().delete(
                            DatabaseContract.MovieEntry.CONTENT_URI,
                            DatabaseContract.MovieEntry._ID + " = ?",
                            new String[]{movieId + ""});
                    ((FloatingActionButton) activity.findViewById(R.id.favorite_button)).setImageResource(R.drawable.ic_favorite_black);
                    isFavorite = false;

                    View view = activity.findViewById(R.id.movie_detail_container);
                    if(view != null && detailFragment.isTwoPane()) {
                        view.setVisibility(View.GONE);
                        activity.setTitle("Movie");
                    }
                }
                else {
                    List<ContentValues> reviewValues = reviewAdapter.toContentValuesList(movieId);
                    List<ContentValues> trailerValues = trailerAdapter.toContentValuesList(movieId);

                    activity.getContentResolver().insert(
                            DatabaseContract.MovieEntry.CONTENT_URI,
                            movieItem.toContentValues());
                    activity.getContentResolver().bulkInsert(
                            DatabaseContract.ReviewEntry.CONTENT_URI,
                            reviewValues.toArray(new ContentValues[reviewValues.size()])
                    );
                    activity.getContentResolver().bulkInsert(
                            DatabaseContract.TrailerEntry.CONTENT_URI,
                            trailerValues.toArray(new ContentValues[trailerValues.size()])
                    );
                    ((FloatingActionButton) activity.findViewById(R.id.favorite_button)).setImageResource(R.drawable.ic_favorite_black);
                    isFavorite = true;
                }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LoaderContract.ALL_REVIEW_LOADER:
                reviewAdapter.swapCursor(null);
                break;
            case LoaderContract.ALL_TRAILER_LOADER:
                trailerAdapter.swapCursor(null);
                break;
        }
    }
}
