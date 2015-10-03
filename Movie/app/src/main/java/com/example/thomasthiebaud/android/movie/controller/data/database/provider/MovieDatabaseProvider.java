package com.example.thomasthiebaud.android.movie.controller.data.database.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.controller.data.http.HttpMethod;
import com.example.thomasthiebaud.android.movie.controller.data.http.HttpService;
import com.example.thomasthiebaud.android.movie.controller.data.http.HttpTask;
import com.example.thomasthiebaud.android.movie.controller.data.http.JsonResponse;
import com.example.thomasthiebaud.android.movie.controller.data.http.MovieHttpCallback;
import com.example.thomasthiebaud.android.movie.model.contract.APIContract;
import com.example.thomasthiebaud.android.movie.model.contract.DatabaseContract;
import com.example.thomasthiebaud.android.movie.controller.data.database.helper.MovieDatabaseHelper;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by thiebaudthomas on 22/09/15.
 */
public class MovieDatabaseProvider extends ContentProvider {
    private final String TAG = MovieDatabaseProvider.class.getSimpleName();
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private SQLiteOpenHelper openHelper;
    private static SQLiteQueryBuilder queryBuilder;

    static final int MOVIE = 100;
    static final int ALL_MOVIE = 101;
    static final int FAVORITE_MOVIE = 102;
    static final int TRAILER = 200;
    static final int REVIEW = 300;

    private HttpMethod httpMethod = HttpMethod.GET;
    private JSONObject body = new JSONObject();
    private JsonResponse response;
    private Exception exception;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, DatabaseContract.PATH_MOVIE + "/*", ALL_MOVIE);
        matcher.addURI(authority, DatabaseContract.PATH_MOVIE + "/#", MOVIE);
        matcher.addURI(authority, DatabaseContract.PATH_MOVIE + "/favorite", FAVORITE_MOVIE);
        matcher.addURI(authority, DatabaseContract.PATH_TRAILER, TRAILER);
        matcher.addURI(authority, DatabaseContract.PATH_REVIEW, REVIEW);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        openHelper = new MovieDatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return DatabaseContract.MovieEntry.CONTENT_TYPE;
            case ALL_MOVIE:
                return DatabaseContract.MovieEntry.CONTENT_TYPE;
            case FAVORITE_MOVIE:
                return DatabaseContract.MovieEntry.CONTENT_TYPE;
            case REVIEW:
                return DatabaseContract.ReviewEntry.CONTENT_TYPE;
            case TRAILER:
                return DatabaseContract.ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case FAVORITE_MOVIE:
                cursor = openHelper.getReadableDatabase().query(
                        DatabaseContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ALL_MOVIE:
                cursor = new MatrixCursor(new String[]{
                        DatabaseContract.MovieEntry._ID,
                        DatabaseContract.MovieEntry.COLUMN_TITLE,
                        DatabaseContract.MovieEntry.COLUMN_POSTER_PATH,
                        DatabaseContract.MovieEntry.COLUMN_OVERVIEW,
                        DatabaseContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                        DatabaseContract.MovieEntry.COLUMN_RELEASE_DATE
                });

                JSONObject json = new HttpService().getMovies(uri.getLastPathSegment() + APIContract.API_SORT_DESC_LABEL).execute();

                JSONArray results;
                try {
                    results = json.getJSONArray(APIContract.JSON_RESULTS);
                    for(int i=0; i<results.length(); i++) {

                        JSONObject jsonMovie = results.getJSONObject(i);

                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme(APIContract.POSTER_SCHEME)
                                .authority(APIContract.POSTER_AUTHORITY)
                                .appendPath(APIContract.POSTER_PATH_T)
                                .appendPath(APIContract.POSTER_PATH_P)
                                .appendPath(APIContract.POSTER_QUALITY);

                        ((MatrixCursor)cursor).addRow(new String[]{
                                jsonMovie.getInt(APIContract.JSON_ID) + "",
                                jsonMovie.getString(APIContract.JSON_TITLE),
                                builder.build().toString() + jsonMovie.getString(APIContract.JSON_POSTER_PATH),
                                jsonMovie.getString(APIContract.JSON_OVERVIEW),
                                jsonMovie.getDouble(APIContract.JSON_VOTE_AVERAGE) + "",
                                jsonMovie.getString(APIContract.JSON_RELEASE_DATE)
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case TRAILER:
                cursor = openHelper.getReadableDatabase().query(
                        DatabaseContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case REVIEW:
                cursor = openHelper.getReadableDatabase().query(
                        DatabaseContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase database = openHelper.getWritableDatabase();

        Uri returnUri = uri;
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                try {
                    long id = database.insert(DatabaseContract.MovieEntry.TABLE_NAME,null,values);
                    Log.e(TAG,id+"");
                }catch (SQLiteConstraintException e) {
                    Log.e(TAG, "UNIQUE constraint not respected ", e);
                }
                break;
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        int rowsDeleted;

        if ( null == selection )
            selection = "1";
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                rowsDeleted = db.delete(DatabaseContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(DatabaseContract.ReviewEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(DatabaseContract.TrailerEntry.TABLE_NAME,selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case TRAILER: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(DatabaseContract.TrailerEntry.TABLE_NAME, null, value);
                        if (id != -1)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case REVIEW: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(DatabaseContract.ReviewEntry.TABLE_NAME, null, value);
                        if (id != -1)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
