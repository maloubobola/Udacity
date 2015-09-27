package com.example.thomasthiebaud.android.movie.controller.fragment;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.thomasthiebaud.android.movie.controller.data.http.MovieHttpCallback;
import com.example.thomasthiebaud.android.movie.controller.data.database.loader.LoaderResponse;
import com.example.thomasthiebaud.android.movie.controller.data.database.loader.MovieLoaderCallback;
import com.example.thomasthiebaud.android.movie.model.contract.APIContract;
import com.example.thomasthiebaud.android.movie.controller.data.adapter.MovieAdapter;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;
import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.controller.activity.DetailActivity;
import com.example.thomasthiebaud.android.movie.controller.data.http.HttpService;

import java.io.IOException;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = MainActivityFragment.class.getSimpleName();

    private MovieAdapter movieAdapter;

    public MainActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gv = (GridView) view.findViewById(R.id.cover_grid);
        movieAdapter = new MovieAdapter(view.getContext());
        gv.setAdapter(movieAdapter);
        gv.setOnItemClickListener(this);

        return view;
    }

    private void updateMovies() {
        String sortBy = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity));

        if(sortBy.equals("favorite"))
            getLoaderManager().initLoader(MovieLoaderCallback.ALL_MOVIE_LOADER, null, new MovieLoaderCallback(getActivity()).onResponse(new LoaderResponse<MovieItem>() {
                @Override
                public void onSuccess(List<MovieItem> items) {
                    movieAdapter.clear();
                    movieAdapter.addAll(items);
                }
            }));
        else {
            new HttpService().getMovies(sortBy + APIContract.API_SORT_DESC_LABEL).onResponse(new MovieHttpCallback() {
                @Override
                public void onSuccess(List<MovieItem> movies) {
                    if (movies != null && !movies.isEmpty()) {
                        movieAdapter.clear();
                        movieAdapter.addAll(movies);
                    }
                }

                @Override
                public void onError(Exception exception) {
                    if (exception instanceof IOException) {
                        getLoaderManager().initLoader(MovieLoaderCallback.ALL_MOVIE_LOADER, null, new MovieLoaderCallback(getActivity()).onResponse(new LoaderResponse<MovieItem>() {
                            @Override
                            public void onSuccess(List<MovieItem> items) {
                                movieAdapter.clear();
                                movieAdapter.addAll(items);
                            }
                        }));
                        Toast.makeText(getActivity(), "Network unreachable.", Toast.LENGTH_LONG).show();
                    }
                }
            }).execute();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(MovieItem.class.getSimpleName(),movieAdapter.getItem(position));
        startActivity(intent);
    }
}
