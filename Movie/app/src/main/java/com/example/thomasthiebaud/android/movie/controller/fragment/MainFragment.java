package com.example.thomasthiebaud.android.movie.controller.fragment;


import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.thomasthiebaud.android.movie.controller.data.adapter.MovieAdapter;
import com.example.thomasthiebaud.android.movie.controller.data.loader.MovieCursorLoaderCallback;
import com.example.thomasthiebaud.android.movie.model.contract.DatabaseContract;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;
import com.example.thomasthiebaud.android.movie.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = MainFragment.class.getSimpleName();

    private MovieAdapter movieAdapter;

    public MainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gv = (GridView) view.findViewById(R.id.cover_grid);
        movieAdapter = new MovieAdapter(getContext(),null,0);
        gv.setAdapter(movieAdapter);
        gv.setOnItemClickListener(this);

        return view;
    }

    private void updateMovies() {
        String sortBy = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity));
        getLoaderManager().restartLoader(MovieCursorLoaderCallback.ALL_MOVIE_LOADER, null, new MovieCursorLoaderCallback(getActivity(), movieAdapter, sortBy));

        View v = getActivity().findViewById(R.id.movie_detail_container);
        if(v != null)
            v.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateMovies();
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

    //TODO MOve to separate file or into activity
    public interface MovieClickCallback {
        void onMovieSelected(MovieItem movie);
    }
}
