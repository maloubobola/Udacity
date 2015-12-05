package it.jaschke.alexandria.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import it.jaschke.alexandria.contract.APIContract;

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

        final String SQL_CREATE_BOOK_TABLE = "CREATE TABLE " + APIContract.BookEntry.TABLE_NAME + " ("+
                APIContract.BookEntry._ID + " INTEGER PRIMARY KEY," +
                APIContract.BookEntry.TITLE + " TEXT NOT NULL," +
                APIContract.BookEntry.SUBTITLE + " TEXT ," +
                APIContract.BookEntry.DESC + " TEXT ," +
                APIContract.BookEntry.IMAGE_URL + " TEXT, " +
                "UNIQUE ("+ APIContract.BookEntry._ID +") ON CONFLICT IGNORE)";

        final String SQL_CREATE_AUTHOR_TABLE = "CREATE TABLE " + APIContract.AuthorEntry.TABLE_NAME + " ("+
                APIContract.AuthorEntry._ID + " INTEGER," +
                APIContract.AuthorEntry.AUTHOR + " TEXT," +
                " FOREIGN KEY (" + APIContract.AuthorEntry._ID + ") REFERENCES " +
                APIContract.BookEntry.TABLE_NAME + " (" + APIContract.BookEntry._ID + "))";

        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + APIContract.CategoryEntry.TABLE_NAME + " ("+
                APIContract.CategoryEntry._ID + " INTEGER," +
                APIContract.CategoryEntry.CATEGORY + " TEXT," +
                " FOREIGN KEY (" + APIContract.CategoryEntry._ID + ") REFERENCES " +
                APIContract.BookEntry.TABLE_NAME + " (" + APIContract.BookEntry._ID + "))";


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
