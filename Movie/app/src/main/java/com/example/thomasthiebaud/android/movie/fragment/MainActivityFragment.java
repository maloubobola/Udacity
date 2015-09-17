package com.example.thomasthiebaud.android.movie.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.thomasthiebaud.android.movie.bean.Constants;
import com.example.thomasthiebaud.android.movie.adapter.MovieAdapter;
import com.example.thomasthiebaud.android.movie.bean.MovieItem;
import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.activity.DetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements AdapterView.OnItemClickListener {

    MovieAdapter movieAdapter;

    public MainActivityFragment() {
    }

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
        MovieTask movieTask = new MovieTask();
        movieTask.execute();
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

    public class MovieTask extends AsyncTask<String,Void,List<MovieItem>> {

        @Override
        protected List<MovieItem> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String json = null;

            URL url = null;
            try {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

                Uri.Builder builder = new Uri.Builder();
                builder.scheme(Constants.API_SCHEME)
                        .authority(Constants.API_AUTHORITY)
                        .appendPath(Constants.API_VERSION)
                        .appendPath(Constants.API_DISCOVER)
                        .appendPath(Constants.API_MOVIE)
                        .appendQueryParameter(Constants.API_SORT_BY_LABEL,prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity))+Constants.API_SORT_DESC_LABEL)
                        .appendQueryParameter(Constants.API_KEY_LABEL,Constants.API_KEY);

                url = new URL(builder.build().toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(Constants.HTTP_GET);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                try {
                    return getCover(new JSONObject(buffer.toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(List<MovieItem> movies) {
            if (movies != null && !movies.isEmpty()) {
                movieAdapter.clear();
                movieAdapter.addAll(movies);
            }
        }
    }

    private List<MovieItem> getCover(JSONObject json) throws JSONException {
        List<MovieItem> movies = new ArrayList<>();
        JSONArray results = json.getJSONArray(Constants.JSON_RESULTS);
        for(int i=0; i<results.length(); i++) {
            MovieItem item = new MovieItem();
            JSONObject jsonMovie = results.getJSONObject(i);

            Uri.Builder builder = new Uri.Builder();
            builder.scheme(Constants.POSTER_SCHEME)
                    .authority(Constants.POSTER_AUTHORITY)
                    .appendPath(Constants.POSTER_PATH_T)
                    .appendPath(Constants.POSTER_PATH_P)
                    .appendPath(Constants.POSTER_QUALITY)
                    .appendPath(jsonMovie.getString(Constants.JSON_POSTER_PATH).replace("/",""));

            item.setPosterPath(builder.build().toString());
            item.setTitle(jsonMovie.getString(Constants.JSON_TITLE));
            item.setId(jsonMovie.getInt(Constants.JSON_ID));
            item.setOverview(jsonMovie.getString(Constants.JSON_OVERVIEW));
            item.setVoteAverage(jsonMovie.getDouble(Constants.JSON_VOTE_AVERAGE));
            item.setReleaseDate(jsonMovie.getString(Constants.JSON_RELEASE_DATE));
            movies.add(item);
        }
        return movies;
    }
}
