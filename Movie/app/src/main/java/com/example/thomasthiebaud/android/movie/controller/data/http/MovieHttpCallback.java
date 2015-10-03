package com.example.thomasthiebaud.android.movie.controller.data.http;

import android.net.Uri;
import android.util.Log;

import com.example.thomasthiebaud.android.movie.model.contract.APIContract;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomasthiebaud on 26/09/15.
 */
public abstract class MovieHttpCallback implements HttpResponse<MovieItem>, JsonResponse {

    @Override
    public void onSuccess(JSONObject object) {
        this.onSuccess(fromJson(object));
    }

    public List<MovieItem> fromJson(JSONObject json) {
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
}
