package com.example.thomasthiebaud.android.movie.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.thomasthiebaud.android.movie.model.Constants;
import com.example.thomasthiebaud.android.movie.adapter.MovieAdapter;
import com.example.thomasthiebaud.android.movie.model.MovieItem;
import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.activity.DetailActivity;
import com.example.thomasthiebaud.android.movie.http.HttpService;
import com.example.thomasthiebaud.android.movie.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = MainActivityFragment.class.getSimpleName();

    MovieAdapter movieAdapter;

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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity)) + Constants.API_SORT_DESC_LABEL;

        new HttpService().getMovies(sortBy).callback(new HttpResponse() {
            @Override
            public void onResponse(JSONObject object) {
                List<MovieItem> movies = getMovieItem(object);
                if (movies != null && !movies.isEmpty()) {
                    movieAdapter.clear();
                    movieAdapter.addAll(movies);
                }
            }
        }).execute();
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

    private List<MovieItem> getMovieItem(JSONObject json) {
        List<MovieItem> movies = new ArrayList<>();
        JSONArray results;
        try {
            results = json.getJSONArray(Constants.JSON_RESULTS);
            for(int i=0; i<results.length(); i++) {
                MovieItem item = new MovieItem();
                JSONObject jsonMovie = results.getJSONObject(i);

                Uri.Builder builder = new Uri.Builder();
                builder.scheme(Constants.POSTER_SCHEME)
                        .authority(Constants.POSTER_AUTHORITY)
                        .appendPath(Constants.POSTER_PATH_T)
                        .appendPath(Constants.POSTER_PATH_P)
                        .appendPath(Constants.POSTER_QUALITY);

                item.setPosterPath(builder.build().toString() + jsonMovie.getString(Constants.JSON_POSTER_PATH));
                item.setTitle(jsonMovie.getString(Constants.JSON_TITLE));
                item.setId(jsonMovie.getInt(Constants.JSON_ID));
                item.setOverview(jsonMovie.getString(Constants.JSON_OVERVIEW));
                item.setVoteAverage(jsonMovie.getDouble(Constants.JSON_VOTE_AVERAGE));
                item.setReleaseDate(jsonMovie.getString(Constants.JSON_RELEASE_DATE));
                movies.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }
}
