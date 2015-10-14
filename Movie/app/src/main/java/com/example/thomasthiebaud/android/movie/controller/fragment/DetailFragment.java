package com.example.thomasthiebaud.android.movie.controller.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.thomasthiebaud.android.movie.controller.data.adapter.ReviewAdapter;
import com.example.thomasthiebaud.android.movie.controller.data.adapter.TrailerAdapter;
import com.example.thomasthiebaud.android.movie.model.contract.DatabaseContract;
import com.example.thomasthiebaud.android.movie.model.contract.LoaderContract;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;
import com.example.thomasthiebaud.android.movie.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = DetailFragment.class.getSimpleName();

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private boolean isFavorite = false;
    private MovieItem item = null;
    private int movieId = 0;

    public DetailFragment() {}

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        reviewAdapter = new ReviewAdapter(rootView.getContext(),null,0);
        trailerAdapter = new TrailerAdapter(rootView.getContext(),null,0);

        Intent intent = getActivity().getIntent();

        item = null;

        Bundle argument = getArguments();

        if(argument != null)
            item = argument.getParcelable(MovieItem.class.getSimpleName());

        if(item == null)
            return rootView;

        ImageView imageView = (ImageView) rootView.findViewById(R.id.poster);

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            imageView.setAdjustViewBounds(true);

        Picasso.with(getContext())
                .load(item.getPosterPath())
                .placeholder(R.drawable.ic_cached_black)
                .error(R.drawable.ic_report_problem_black)
                .into(imageView);

        //Use navbar to display title
        getActivity().setTitle(item.getTitle());

        ((TextView)rootView.findViewById(R.id.overview)).setText(item.getOverview());
        ((TextView)rootView.findViewById(R.id.date)).setText(item.getReleaseDate());
        ((RatingBar)rootView.findViewById(R.id.ratingBar)).setRating((float) item.getVoteAverage());

        movieId = item.getId();

        ListView reviewsList = ((ListView) rootView.findViewById(R.id.reviews_list));
        reviewsList.setAdapter(reviewAdapter);

        ListView trailersList = ((ListView) rootView.findViewById(R.id.trailers_list));
        trailersList.setAdapter(trailerAdapter);

        getLoaderManager().restartLoader(LoaderContract.ONE_MOVIE_LOADER, null, this);

        getLoaderManager().restartLoader(LoaderContract.ALL_REVIEW_LOADER, null, this);

        getLoaderManager().restartLoader(LoaderContract.ALL_TRAILER_LOADER, null, this);

        ((ListView)rootView.findViewById(R.id.trailers_list)).setEmptyView(rootView.findViewById(R.id.empty_trailers_label));
        ((ListView)rootView.findViewById(R.id.reviews_list)).setEmptyView(rootView.findViewById(R.id.empty_reviews_label));

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case LoaderContract.ONE_MOVIE_LOADER:
                cursorLoader = new CursorLoader(getActivity(),
                        DatabaseContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieId + "").build(),
                        null,
                        DatabaseContract.ReviewEntry._ID + "= ?",
                        new String[]{movieId + ""},
                        null);
                break;
            case LoaderContract.ALL_REVIEW_LOADER:
                cursorLoader = new CursorLoader(getActivity(),
                        DatabaseContract.ReviewEntry.CONTENT_URI.buildUpon().appendPath("popularity").appendPath(movieId+"").build(),
                        null,
                        DatabaseContract.ReviewEntry.COLUMN_ID_MOVIE + "= ?",
                        new String[]{movieId + ""},
                        null);
                break;
            case LoaderContract.ALL_TRAILER_LOADER:
                cursorLoader = new CursorLoader(getActivity(),
                        DatabaseContract.TrailerEntry.CONTENT_URI.buildUpon().appendPath("popularity").appendPath(movieId+"").build(),
                        null,
                        DatabaseContract.TrailerEntry.COLUMN_ID_MOVIE + "= ?",
                        new String[]{movieId + ""},
                        null);
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LoaderContract.ONE_MOVIE_LOADER:
                Log.i(TAG, data.getCount()+"");
                if(data.moveToFirst()) {
                    ((FloatingActionButton) getActivity().findViewById(R.id.favorite_button)).setImageResource(R.drawable.ic_favorite_black);
                    isFavorite = true;
                }
                else {
                    ((FloatingActionButton) getActivity().findViewById(R.id.favorite_button)).setImageResource(R.drawable.ic_favorite_border_black);
                    isFavorite = false;
                }
                break;
            case LoaderContract.ALL_REVIEW_LOADER:
                if(data.moveToFirst()) {
                    while (data.moveToNext()) {
                        reviewAdapter.swapCursor(data);
                    }
                }
                break;
            case LoaderContract.ALL_TRAILER_LOADER:
                if(data.moveToFirst()) {
                    while (data.moveToNext()) {
                        trailerAdapter.swapCursor(data);
                    }
                }
                break;
        }

        final ContentValues movieValues = item.toContentValues();
        final List<ContentValues> reviewValues = new ArrayList<>();
        final List<ContentValues> trailerValues = new ArrayList<>();

        getActivity().findViewById(R.id.favorite_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    getContext().getContentResolver().delete(
                            DatabaseContract.ReviewEntry.CONTENT_URI,
                            DatabaseContract.ReviewEntry.COLUMN_ID_MOVIE + " = ?",
                            new String[]{movieId + ""});
                    getContext().getContentResolver().delete(
                            DatabaseContract.TrailerEntry.CONTENT_URI,
                            DatabaseContract.TrailerEntry.COLUMN_ID_MOVIE + " = ?",
                            new String[]{movieId + ""});
                    getContext().getContentResolver().delete(
                            DatabaseContract.MovieEntry.CONTENT_URI,
                            DatabaseContract.MovieEntry._ID + " = ?",
                            new String[]{movieId + ""});
                    ((FloatingActionButton) getActivity().findViewById(R.id.favorite_button)).setImageResource(R.drawable.ic_favorite_black);
                    isFavorite = false;
                }
                else {
                    Cursor cursor = reviewAdapter.getCursor();
                    if(cursor != null && cursor.moveToFirst()) {
                        while(cursor.moveToNext()) {
                            ContentValues value = new ContentValues();
                            value.put(DatabaseContract.ReviewEntry._ID, cursor.getString(DatabaseContract.ReviewEntry._ID_INDEX));
                            value.put(DatabaseContract.ReviewEntry.COLUMN_AUTHOR, cursor.getString(DatabaseContract.ReviewEntry.COLUMN_AUTHOR_INDEX));
                            value.put(DatabaseContract.ReviewEntry.COLUMN_CONTENT, cursor.getString(DatabaseContract.ReviewEntry.COLUMN_CONTENT_INDEX));
                            value.put(DatabaseContract.ReviewEntry.COLUMN_URL, cursor.getString(DatabaseContract.ReviewEntry.COLUMN_URL_INDEX));
                            value.put(DatabaseContract.ReviewEntry.COLUMN_ID_MOVIE, movieId);
                            reviewValues.add(value);
                        }
                    }

                    cursor = trailerAdapter.getCursor();
                    if(cursor != null && cursor.moveToFirst()) {
                        while(cursor.moveToNext()) {
                            ContentValues value = new ContentValues();
                            value.put(DatabaseContract.TrailerEntry._ID, cursor.getString(DatabaseContract.TrailerEntry._ID_INDEX));
                            value.put(DatabaseContract.TrailerEntry.COLUMN_KEY, cursor.getString(DatabaseContract.TrailerEntry.COLUMN_KEY_INDEX));
                            value.put(DatabaseContract.TrailerEntry.COLUMN_NAME, cursor.getString(DatabaseContract.TrailerEntry.COLUMN_NAME_INDEX));
                            value.put(DatabaseContract.TrailerEntry.COLUMN_ID_MOVIE, movieId);
                            trailerValues.add(value);
                        }
                    }

                    getContext().getContentResolver().insert(
                            DatabaseContract.MovieEntry.CONTENT_URI,
                            movieValues);
                    getContext().getContentResolver().bulkInsert(
                            DatabaseContract.ReviewEntry.CONTENT_URI,
                            reviewValues.toArray(new ContentValues[reviewValues.size()])
                    );
                    getContext().getContentResolver().bulkInsert(
                            DatabaseContract.TrailerEntry.CONTENT_URI,
                            trailerValues.toArray(new ContentValues[trailerValues.size()])
                    );
                    ((FloatingActionButton) getActivity().findViewById(R.id.favorite_button)).setImageResource(R.drawable.ic_favorite_black);
                    isFavorite = true;
                }

            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
