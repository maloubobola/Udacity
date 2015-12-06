package it.jaschke.alexandria.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import it.jaschke.alexandria.contract.DatabaseContract;

/**
 * Created by saj on 24/12/14.
 */
public class BookProvider extends ContentProvider {

    private static final int BOOK_ID = 100;
    private static final int BOOK = 101;

    private static final int AUTHOR_ID = 200;
    private static final int AUTHOR = 201;

    private static final int CATEGORY_ID = 300;
    private static final int CATEGORY = 301;

    private static final int BOOK_FULL = 500;
    private static final int BOOK_FULLDETAIL = 501;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private DbHelper dbHelper;

    private static final SQLiteQueryBuilder bookFull;

    static{
        bookFull = new SQLiteQueryBuilder();
        bookFull.setTables(
                DatabaseContract.BookEntry.TABLE_NAME + " LEFT OUTER JOIN " +
                DatabaseContract.AuthorEntry.TABLE_NAME + " USING (" + DatabaseContract.BookEntry._ID + ")" +
                " LEFT OUTER JOIN " +  DatabaseContract.CategoryEntry.TABLE_NAME + " USING (" + DatabaseContract.BookEntry._ID + ")");
    }


    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DatabaseContract.PATH_BOOKS+"/#", BOOK_ID);
        matcher.addURI(authority, DatabaseContract.PATH_AUTHORS+"/#", AUTHOR_ID);
        matcher.addURI(authority, DatabaseContract.PATH_CATEGORIES+"/#", CATEGORY_ID);

        matcher.addURI(authority, DatabaseContract.PATH_BOOKS, BOOK);
        matcher.addURI(authority, DatabaseContract.PATH_AUTHORS, AUTHOR);
        matcher.addURI(authority, DatabaseContract.PATH_CATEGORIES, CATEGORY);

        matcher.addURI(authority, DatabaseContract.PATH_FULLBOOK +"/#", BOOK_FULLDETAIL);
        matcher.addURI(authority, DatabaseContract.PATH_FULLBOOK, BOOK_FULL);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            case BOOK:
                retCursor=dbHelper.getReadableDatabase().query(
                        DatabaseContract.BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selection==null? null : selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case AUTHOR:
                retCursor=dbHelper.getReadableDatabase().query(
                        DatabaseContract.AuthorEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CATEGORY:
                retCursor=dbHelper.getReadableDatabase().query(
                        DatabaseContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case BOOK_ID:
                retCursor=dbHelper.getReadableDatabase().query(
                        DatabaseContract.BookEntry.TABLE_NAME,
                        projection,
                        DatabaseContract.BookEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case AUTHOR_ID:
                retCursor=dbHelper.getReadableDatabase().query(
                        DatabaseContract.AuthorEntry.TABLE_NAME,
                        projection,
                        DatabaseContract.AuthorEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CATEGORY_ID:
                retCursor=dbHelper.getReadableDatabase().query(
                        DatabaseContract.CategoryEntry.TABLE_NAME,
                        projection,
                        DatabaseContract.CategoryEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case BOOK_FULLDETAIL:
                String[] bfd_projection ={
                    DatabaseContract.BookEntry.TABLE_NAME + "." + DatabaseContract.BookEntry.TITLE,
                    DatabaseContract.BookEntry.TABLE_NAME + "." + DatabaseContract.BookEntry.SUBTITLE,
                    DatabaseContract.BookEntry.TABLE_NAME + "." + DatabaseContract.BookEntry.IMAGE_URL,
                    DatabaseContract.BookEntry.TABLE_NAME + "." + DatabaseContract.BookEntry.DESC,
                    "group_concat(DISTINCT " + DatabaseContract.AuthorEntry.TABLE_NAME+ "."+ DatabaseContract.AuthorEntry.AUTHOR +") as " + DatabaseContract.AuthorEntry.AUTHOR,
                    "group_concat(DISTINCT " + DatabaseContract.CategoryEntry.TABLE_NAME+ "."+ DatabaseContract.CategoryEntry.CATEGORY +") as " + DatabaseContract.CategoryEntry.CATEGORY
                };
                retCursor = bookFull.query(dbHelper.getReadableDatabase(),
                        bfd_projection,
                        DatabaseContract.BookEntry.TABLE_NAME + "." + DatabaseContract.BookEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        DatabaseContract.BookEntry.TABLE_NAME + "." + DatabaseContract.BookEntry._ID,
                        null,
                        sortOrder);
                break;
            case BOOK_FULL:
                String[] bf_projection ={
                        DatabaseContract.BookEntry.TABLE_NAME + "." + DatabaseContract.BookEntry.TITLE,
                        DatabaseContract.BookEntry.TABLE_NAME + "." + DatabaseContract.BookEntry.IMAGE_URL,
                        "group_concat(DISTINCT " + DatabaseContract.AuthorEntry.TABLE_NAME+ "."+ DatabaseContract.AuthorEntry.AUTHOR + ") as " + DatabaseContract.AuthorEntry.AUTHOR,
                        "group_concat(DISTINCT " + DatabaseContract.CategoryEntry.TABLE_NAME+ "."+ DatabaseContract.CategoryEntry.CATEGORY +") as " + DatabaseContract.CategoryEntry.CATEGORY
                };
                retCursor = bookFull.query(dbHelper.getReadableDatabase(),
                        bf_projection,
                        null,
                        selectionArgs,
                        DatabaseContract.BookEntry.TABLE_NAME + "." + DatabaseContract.BookEntry._ID,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }



    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case BOOK_FULLDETAIL:
                return DatabaseContract.BookEntry.CONTENT_ITEM_TYPE;
            case BOOK_ID:
                return DatabaseContract.BookEntry.CONTENT_ITEM_TYPE;
            case AUTHOR_ID:
                return DatabaseContract.AuthorEntry.CONTENT_ITEM_TYPE;
            case CATEGORY_ID:
                return DatabaseContract.CategoryEntry.CONTENT_ITEM_TYPE;
            case BOOK:
                return DatabaseContract.BookEntry.CONTENT_TYPE;
            case AUTHOR:
                return DatabaseContract.AuthorEntry.CONTENT_TYPE;
            case CATEGORY:
                return DatabaseContract.CategoryEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case BOOK: {
                long _id = db.insert(DatabaseContract.BookEntry.TABLE_NAME, null, values);
                if ( _id > 0 ){
                    returnUri = DatabaseContract.BookEntry.buildBookUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                getContext().getContentResolver().notifyChange(DatabaseContract.BookEntry.buildFullBookUri(_id), null);
                break;
            }
            case AUTHOR:{
                long _id = db.insert(DatabaseContract.AuthorEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DatabaseContract.AuthorEntry.buildAuthorUri(values.getAsLong("_id"));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CATEGORY: {
                long _id = db.insert(DatabaseContract.CategoryEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DatabaseContract.CategoryEntry.buildCategoryUri(values.getAsLong("_id"));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case BOOK:
                rowsDeleted = db.delete(
                        DatabaseContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case AUTHOR:
                rowsDeleted = db.delete(
                        DatabaseContract.AuthorEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CATEGORY:
                rowsDeleted = db.delete(
                        DatabaseContract.CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                rowsDeleted = db.delete(
                        DatabaseContract.BookEntry.TABLE_NAME,
                        DatabaseContract.BookEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case BOOK:
                rowsUpdated = db.update(DatabaseContract.BookEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case AUTHOR:
                rowsUpdated = db.update(DatabaseContract.AuthorEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CATEGORY:
                rowsUpdated = db.update(DatabaseContract.CategoryEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}