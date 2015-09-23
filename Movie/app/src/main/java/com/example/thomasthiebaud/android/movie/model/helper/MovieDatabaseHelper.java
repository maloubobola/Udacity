package com.example.thomasthiebaud.android.movie.model.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.thomasthiebaud.android.movie.model.contract.DatabaseContract;

/**
 * Created by thiebaudthomas on 22/09/15.
 */
public class MovieDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movie.db";

    public MovieDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + DatabaseContract.MovieEntry.TABLE_NAME + " (" +
                DatabaseContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                DatabaseContract.MovieEntry.COLUMN_TITLE + " VARCHAR(100) NOT NULL, " +
                DatabaseContract.MovieEntry.COLUMN_POSTER_PATH + " VARCHAR(500) NOT NULL, " +
                DatabaseContract.MovieEntry.COLUMN_OVERVIEW + " VARCHAR(1000) NOT NULL, " +
                DatabaseContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DOUBLE NOT NULL, " +
                DatabaseContract.MovieEntry.COLUMN_RELEASE_DATE + " VARCHAR(45) NOT NULL " +
                " );";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + DatabaseContract.TrailerEntry.TABLE_NAME + " (" +
                DatabaseContract.TrailerEntry._ID + " VARCHAR(50) PRIMARY KEY, " +
                DatabaseContract.TrailerEntry.COLUMN_NAME + " VARCHAR(50) NOT NULL, " +
                DatabaseContract.TrailerEntry.COLUMN_KEY + " VARCHAR(50) NOT NULL, " +
                DatabaseContract.TrailerEntry.COLUMN_ID_MOVIE + " INT NOT NULL, " +

                " FOREIGN KEY (" + DatabaseContract.TrailerEntry.COLUMN_ID_MOVIE + ") REFERENCES " +
                DatabaseContract.MovieEntry.TABLE_NAME + " (" + DatabaseContract.MovieEntry._ID + "));";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + DatabaseContract.ReviewEntry.TABLE_NAME + " (" +
                DatabaseContract.ReviewEntry._ID + " VARCHAR(50) PRIMARY KEY, " +
                DatabaseContract.ReviewEntry.COLUMN_AUTHOR + " VARCHAR(50) NOT NULL, " +
                DatabaseContract.ReviewEntry.COLUMN_CONTENT + " VARCHAR(1000) NOT NULL, " +
                DatabaseContract.ReviewEntry.COLUMN_URL + " VARCHAR(200) NOT NULL, " +
                DatabaseContract.ReviewEntry.COLUMN_ID_MOVIE + " INT NOT NULL, " +

                " FOREIGN KEY (" + DatabaseContract.ReviewEntry.COLUMN_ID_MOVIE + ") REFERENCES " +
                DatabaseContract.MovieEntry.TABLE_NAME + " (" + DatabaseContract.MovieEntry._ID + "));";

        database.execSQL(SQL_CREATE_MOVIE_TABLE);
        database.execSQL(SQL_CREATE_TRAILER_TABLE);
        database.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ReviewEntry.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TrailerEntry.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.MovieEntry.TABLE_NAME);
        onCreate(database);
    }
}
