package com.example.thomasthiebaud.android.movie.app.main;


import android.database.Cursor;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.thomasthiebaud.android.movie.model.adapter.MovieAdapter;
import com.example.thomasthiebaud.android.movie.contract.DatabaseContract;
import com.example.thomasthiebaud.android.movie.contract.LoaderContract;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;
import com.example.thomasthiebaud.android.movie.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = MainFragment.class.getSimpleName();
    private MovieAdapter movieAdapter;

    private MainCursorLoaderCallback mainCursorLoaderCallback;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String ext = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.e(TAG,ext);

        GridView gv = (GridView) rootView.findViewById(R.id.cover_grid);
        movieAdapter = new MovieAdapter(getContext(),null,0);
        gv.setAdapter(movieAdapter);
        gv.setOnItemClickListener(this);

        mainCursorLoaderCallback = new MainCursorLoaderCallback(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String sortBy = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity));
        mainCursorLoaderCallback.setSortOrder(sortBy);
        getLoaderManager().initLoader(LoaderContract.ALL_MOVIE_LOADER, null, mainCursorLoaderCallback);

        View v = getActivity().findViewById(R.id.movie_detail_container);
        if(v != null)
            v.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        if(cursor != null) {
            MovieItem movie = new MovieItem();
            movie.setId(cursor.getInt(DatabaseContract.MovieEntry._ID_INDEX));
            movie.setTitle(cursor.getString(DatabaseContract.MovieEntry.COLUMN_TITLE_INDEX));
            movie.setPosterPath(cursor.getString(DatabaseContract.MovieEntry.COLUMN_POSTER_PATH_INDEX));
            movie.setOverview(cursor.getString(DatabaseContract.MovieEntry.COLUMN_OVERVIEW_INDEX));
            movie.setVoteAverage(cursor.getDouble(DatabaseContract.MovieEntry.COLUMN_VOTE_AVERAGE_INDEX));
            movie.setReleaseDate(cursor.getString(DatabaseContract.MovieEntry.COLUMN_RELEASE_DATE_INDEX));

            ((MovieClickCallback) getActivity()).onMovieSelected(movie);

            View v = getActivity().findViewById(R.id.movie_detail_container);
            if(v != null)
                v.setVisibility(View.VISIBLE);
        }
    }

    public void onSortByChanged() {
        String sortBy = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity));
        getLoaderManager().restartLoader(LoaderContract.ALL_MOVIE_LOADER, null, mainCursorLoaderCallback.setSortOrder(sortBy));
    }

    //TODO MOve to separate file or into activity
    public interface MovieClickCallback {
        void onMovieSelected(MovieItem movie);
    }

    public MovieAdapter getMovieAdapter() {
        return movieAdapter;
    }

    public View getRootView() {
        return rootView;
    }
}
