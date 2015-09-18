package com.example.thomasthiebaud.android.movie.http;

import com.example.thomasthiebaud.android.movie.model.Constants;

/**
 * Created by thiebaudthomas on 18/09/15.
 */
public class HttpService {
    public HttpTask getMovies(String sortBy) {
        return HttpTask
                .authority(Constants.API_AUTHORITY)
                .scheme(Constants.API_SCHEME)
                .appendPath(Constants.API_VERSION)
                .appendPath(Constants.API_DISCOVER)
                .appendPath(Constants.API_MOVIE)
                .appendQuery(Constants.API_SORT_BY_LABEL,sortBy)
                .appendQuery(Constants.API_KEY_LABEL,Constants.API_KEY);
    }

    public HttpTask getVideos(String id) {
        return HttpTask
                .authority(Constants.API_AUTHORITY)
                .scheme(Constants.API_SCHEME)
                .appendPath(Constants.API_VERSION)
                .appendPath(Constants.API_MOVIE)
                .appendPath(id)
                .appendPath(Constants.API_VIDEOS)
                .appendQuery(Constants.API_KEY_LABEL, Constants.API_KEY);
    }

    public HttpTask getReview(String id) {
        return HttpTask
                .authority(Constants.API_AUTHORITY)
                .scheme(Constants.API_SCHEME)
                .appendPath(Constants.API_VERSION)
                .appendPath(Constants.API_MOVIE)
                .appendPath(id)
                .appendPath(Constants.API_REVIEWS)
                .appendQuery(Constants.API_KEY_LABEL, Constants.API_KEY);
    }
}
