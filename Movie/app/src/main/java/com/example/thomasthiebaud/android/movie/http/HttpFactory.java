package com.example.thomasthiebaud.android.movie.http;

import com.example.thomasthiebaud.android.movie.contract.APIContract;

/**
 * Created by thiebaudthomas on 18/09/15.
 */
public class HttpFactory {
    public HttpTask getMovies(String sortBy) {
        return HttpTask
                .authority(APIContract.API_AUTHORITY)
                .scheme(APIContract.API_SCHEME)
                .appendPath(APIContract.API_VERSION)
                .appendPath(APIContract.API_DISCOVER)
                .appendPath(APIContract.API_MOVIE)
                .appendQuery(APIContract.API_SORT_BY_LABEL,sortBy)
                .appendQuery(APIContract.API_KEY_LABEL, APIContract.API_KEY);
    }

    public HttpTask getTrailers(String id) {
        return HttpTask
                .authority(APIContract.API_AUTHORITY)
                .scheme(APIContract.API_SCHEME)
                .appendPath(APIContract.API_VERSION)
                .appendPath(APIContract.API_MOVIE)
                .appendPath(id)
                .appendPath(APIContract.API_VIDEOS)
                .appendQuery(APIContract.API_KEY_LABEL, APIContract.API_KEY);
    }

    public HttpTask getReview(String id) {
        return HttpTask
                .authority(APIContract.API_AUTHORITY)
                .scheme(APIContract.API_SCHEME)
                .appendPath(APIContract.API_VERSION)
                .appendPath(APIContract.API_MOVIE)
                .appendPath(id)
                .appendPath(APIContract.API_REVIEWS)
                .appendQuery(APIContract.API_KEY_LABEL, APIContract.API_KEY);
    }
}
