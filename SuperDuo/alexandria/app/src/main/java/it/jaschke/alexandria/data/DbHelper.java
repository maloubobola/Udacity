package it.jaschke.alexandria.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import it.jaschke.alexandria.contract.DatabaseContract;

/**
 * Created by saj on 22/12/14.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "alexandria.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_BOOK_TABLE = "CREATE TABLE " + DatabaseContract.BookEntry.TABLE_NAME + " ("+
                DatabaseContract.BookEntry._ID + " INTEGER PRIMARY KEY," +
                DatabaseContract.BookEntry.TITLE + " TEXT NOT NULL," +
                DatabaseContract.BookEntry.SUBTITLE + " TEXT ," +
                DatabaseContract.BookEntry.DESC + " TEXT ," +
                DatabaseContract.BookEntry.IMAGE_URL + " TEXT, " +
                "UNIQUE ("+ DatabaseContract.BookEntry._ID +") ON CONFLICT IGNORE)";

        final String SQL_CREATE_AUTHOR_TABLE = "CREATE TABLE " + DatabaseContract.AuthorEntry.TABLE_NAME + " ("+
                DatabaseContract.AuthorEntry._ID + " INTEGER," +
                DatabaseContract.AuthorEntry.AUTHOR + " TEXT," +
                " FOREIGN KEY (" + DatabaseContract.AuthorEntry._ID + ") REFERENCES " +
                DatabaseContract.BookEntry.TABLE_NAME + " (" + DatabaseContract.BookEntry._ID + "))";

        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + DatabaseContract.CategoryEntry.TABLE_NAME + " ("+
                DatabaseContract.CategoryEntry._ID + " INTEGER," +
                DatabaseContract.CategoryEntry.CATEGORY + " TEXT," +
                " FOREIGN KEY (" + DatabaseContract.CategoryEntry._ID + ") REFERENCES " +
                DatabaseContract.BookEntry.TABLE_NAME + " (" + DatabaseContract.BookEntry._ID + "))";


        Log.d("sql-statments",SQL_CREATE_BOOK_TABLE);
        Log.d("sql-statments",SQL_CREATE_AUTHOR_TABLE);
        Log.d("sql-statments",SQL_CREATE_CATEGORY_TABLE);

        db.execSQL(SQL_CREATE_BOOK_TABLE);
        db.execSQL(SQL_CREATE_AUTHOR_TABLE);
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
