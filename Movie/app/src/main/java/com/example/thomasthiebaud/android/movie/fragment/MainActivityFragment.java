package com.example.thomasthiebaud.android.movie.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.thomasthiebaud.android.movie.model.contract.APIContract;
import com.example.thomasthiebaud.android.movie.adapter.MovieAdapter;
import com.example.thomasthiebaud.android.movie.model.contract.DatabaseContract;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;
import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.activity.DetailActivity;
import com.example.thomasthiebaud.android.movie.http.HttpService;
import com.example.thomasthiebaud.android.movie.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
        String sortBy = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity));

        //Update title to show sort order
        getActivity().setTitle("Movie - Sort by " + sortBy);

        if(sortBy.equals("favorite")) {
            Cursor cursor = getActivity().getContentResolver().query(
                    DatabaseContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            movieAdapter.clear();
            movieAdapter.addAll(cursorToList(cursor));
        }
        else {
            new HttpService().getMovies(sortBy + APIContract.API_SORT_DESC_LABEL).callback(new HttpResponse() {
                @Override
                public void onResponse(JSONObject object) {
                    List<MovieItem> movies = getMovieItem(object);
                    if (movies != null && !movies.isEmpty()) {
                        movieAdapter.clear();
                        movieAdapter.addAll(movies);
                    }
                }

                @Override
                public void onError(Exception exception) {
                    if(exception instanceof IOException) {
                        Cursor cursor = getActivity().getContentResolver().query(
                                DatabaseContract.MovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null
                        );
                        movieAdapter.clear();
                        movieAdapter.addAll(cursorToList(cursor));

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

    private List<MovieItem> getMovieItem(JSONObject json) {
        List<MovieItem> movies = new ArrayList<>();
        JSONArray results;
        try {
            results = json.getJSONArray(APIContract.JSON_RESULTS);
            for(int i=0; i<results.length(); i++) {
                MovieItem item = new MovieItem();
                JSONObject jsonMovie = results.getJSONObject(i);

                Uri.Builder builder = new Uri.Builder();
                builder.scheme(APIContract.POSTER_SCHEME)
                        .authority(APIContract.POSTER_AUTHORITY)
                        .appendPath(APIContract.POSTER_PATH_T)
                        .appendPath(APIContract.POSTER_PATH_P)
                        .appendPath(APIContract.POSTER_QUALITY);

                item.setPosterPath(builder.build().toString() + jsonMovie.getString(APIContract.JSON_POSTER_PATH));
                item.setTitle(jsonMovie.getString(APIContract.JSON_TITLE));
                item.setId(jsonMovie.getInt(APIContract.JSON_ID));
                item.setOverview(jsonMovie.getString(APIContract.JSON_OVERVIEW));
                item.setVoteAverage(jsonMovie.getDouble(APIContract.JSON_VOTE_AVERAGE));
                item.setReleaseDate(jsonMovie.getString(APIContract.JSON_RELEASE_DATE));
                movies.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    private List<MovieItem> cursorToList(Cursor cursor) {
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
