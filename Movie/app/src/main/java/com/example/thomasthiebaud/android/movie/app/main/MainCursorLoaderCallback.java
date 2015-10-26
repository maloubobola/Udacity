package com.example.thomasthiebaud.android.movie.app.main;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.widget.TextView;

import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.commons.loader.CustomCursorLoader;
import com.example.thomasthiebaud.android.movie.commons.loader.LoaderException;
import com.example.thomasthiebaud.android.movie.contract.DatabaseContract;
import com.example.thomasthiebaud.android.movie.contract.LoaderContract;

/**
 * Created by thomasthiebaud on 03/10/15.
 */
public class MainCursorLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = MainCursorLoaderCallback.class.getSimpleName();

    private MainFragment mainFragment;

    private CursorAdapter adapter;
    private String sortBy;

    public MainCursorLoaderCallback(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
        this.adapter = mainFragment.getMovieAdapter();
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
                cursorLoader =  new CustomCursorLoader(mainFragment.getActivity(),
                        DatabaseContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(sortBy).build(),
                        null,
                        null,
                        null,
                        null);
                break;
            case LoaderContract.ONE_MOVIE_LOADER:
                cursorLoader =  new CursorLoader(mainFragment.getActivity(),
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) throws LoaderException {
        CustomCursorLoader l = (CustomCursorLoader) loader;
        TextView textView = (TextView) mainFragment.getRootView().findViewById(R.id.empty_movies_label);

        if(l.getThrowable() != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText("Network unreachable.\n Only favorites are available.\n Go to (Settings -> favorite).");
            adapter.swapCursor(null);
        }
        else if(adapter != null && data != null) {
            adapter.swapCursor(data);
            if(data.getCount() <= 0)
                textView.setText("No movies");
            else
                textView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
