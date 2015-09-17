package com.example.thomasthiebaud.android.movie.bean;

/**
 * Created by thiebaudthomas on 10/09/15.
 */
public interface Constants {
    //The API KEY MUST NOT BE PUBLISH. Is it possible to generate a new one for free from www.themoviedb.org
    String API_KEY = "";

    String API_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
    String API_SORT_BY_LABEL = "sort_by=";
    String API_SORT_DESC_LABEL = ".desc";
    String API_KEY_LABEL = "&api_key=";

    String JSON_RESULTS = "results";
    String JSON_ID = "id";
    String JSON_TITLE = "title";
    String JSON_OVERVIEW = "overview";
    String JSON_VOTE_AVERAGE = "vote_average";
    String JSON_RELEASE_DATE = "release_date";
    String JSON_POSTER_PATH = "poster_path";

    String POSTER_FORMAT = "w500";//"w185";
    String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

    String HTTP_GET = "GET";
}
