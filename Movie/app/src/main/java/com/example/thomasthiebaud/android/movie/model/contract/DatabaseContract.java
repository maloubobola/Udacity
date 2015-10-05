package com.example.thomasthiebaud.android.movie.model.contract;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by thiebaudthomas on 21/09/15.
 */
public class DatabaseContract {
    public static final String CONTENT_AUTHORITY = "com.example.thomasthiebaud.android.movie";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_TRAILER = "trailer";

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";

        public static final int _ID_INDEX = 0;
        public static final int COLUMN_TITLE_INDEX = 1;
        public static final int COLUMN_POSTER_PATH_INDEX = 2;
        public static final int COLUMN_OVERVIEW_INDEX = 3;
        public static final int COLUMN_VOTE_AVERAGE_INDEX = 4;
        public static final int COLUMN_RELEASE_DATE_INDEX = 5;

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
    }

    public static final class ReviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "review";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_ID_MOVIE = "idMovie";

        public static final int _ID_INDEX = 0;
        public static final int COLUMN_AUTHOR_INDEX = 1;
        public static final int COLUMN_CONTENT_INDEX = 2;
        public static final int COLUMN_URL_INDEX = 3;
        public static final int COLUMN_ID_MOVIE_INDEX = 4;

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();
    }

    public static final class TrailerEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_ID_MOVIE = "idMovie";

        public static final int _ID_INDEX = 0;
        public static final int COLUMN_NAME_INDEX = 1;
        public static final int COLUMN_KEY_INDEX = 2;
        public static final int COLUMN_ID_MOVIE_INDEX = 3;

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();
    }
}
