package it.jaschke.alexandria;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import it.jaschke.alexandria.contract.APIContract;

/**
 * Created by saj on 23/12/14.
 */
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void setUp() {
        deleteAllRecords();
    }

    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                APIContract.BookEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                APIContract.CategoryEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                APIContract.AuthorEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                APIContract.BookEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                APIContract.AuthorEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                APIContract.CategoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void testGetType() {

        String type = mContext.getContentResolver().getType(APIContract.BookEntry.CONTENT_URI);
        assertEquals(APIContract.BookEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(APIContract.AuthorEntry.CONTENT_URI);
        assertEquals(APIContract.AuthorEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(APIContract.CategoryEntry.CONTENT_URI);
        assertEquals(APIContract.CategoryEntry.CONTENT_TYPE, type);

        long id = 9780137903955L;
        type = mContext.getContentResolver().getType(APIContract.BookEntry.buildBookUri(id));
        assertEquals(APIContract.BookEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(APIContract.BookEntry.buildFullBookUri(id));
        assertEquals(APIContract.BookEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(APIContract.AuthorEntry.buildAuthorUri(id));
        assertEquals(APIContract.AuthorEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(APIContract.CategoryEntry.buildCategoryUri(id));
        assertEquals(APIContract.CategoryEntry.CONTENT_ITEM_TYPE, type);

    }

    public void testInsertRead(){

        insertReadBook();
        insertReadAuthor();
        insertReadCategory();

        readFullBook();
        readFullList();
    }

    public void insertReadBook(){
        ContentValues bookValues = TestDb.getBookValues();

        Uri bookUri = mContext.getContentResolver().insert(APIContract.BookEntry.CONTENT_URI, bookValues);
        long bookRowId = ContentUris.parseId(bookUri);
        assertTrue(bookRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                APIContract.BookEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, bookValues);

        cursor = mContext.getContentResolver().query(
                APIContract.BookEntry.buildBookUri(bookRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, bookValues);

    }

    public void insertReadAuthor(){
        ContentValues authorValues = TestDb.getAuthorValues();

        Uri authorUri = mContext.getContentResolver().insert(APIContract.AuthorEntry.CONTENT_URI, authorValues);
        long authorRowId = ContentUris.parseId(authorUri);
        assertTrue(authorRowId != -1);
        assertEquals(authorRowId,TestDb.ean);

        Cursor cursor = mContext.getContentResolver().query(
                APIContract.AuthorEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, authorValues);

        cursor = mContext.getContentResolver().query(
                APIContract.AuthorEntry.buildAuthorUri(authorRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, authorValues);

    }

    public void insertReadCategory(){
        ContentValues categoryValues = TestDb.getCategoryValues();

        Uri categoryUri = mContext.getContentResolver().insert(APIContract.CategoryEntry.CONTENT_URI, categoryValues);
        long categoryRowId = ContentUris.parseId(categoryUri);
        assertTrue(categoryRowId != -1);
        assertEquals(categoryRowId,TestDb.ean);

        Cursor cursor = mContext.getContentResolver().query(
                APIContract.CategoryEntry.CONTENT_URI,
                null, // projection
                null, // selection
                null, // selection args
                null  // sort order
        );

        TestDb.validateCursor(cursor, categoryValues);

        cursor = mContext.getContentResolver().query(
                APIContract.CategoryEntry.buildCategoryUri(categoryRowId),
                null, // projection
                null, // selection
                null, // selection args
                null  // sort order
        );

        TestDb.validateCursor(cursor, categoryValues);

    }

    public void readFullBook(){

        Cursor cursor = mContext.getContentResolver().query(
                APIContract.BookEntry.buildFullBookUri(TestDb.ean),
                null, // projection
                null, // selection
                null, // selection args
                null  // sort order
        );

         TestDb.validateCursor(cursor, TestDb.getFullDetailValues());
    }

    public void readFullList(){

        Cursor cursor = mContext.getContentResolver().query(
                APIContract.BookEntry.FULL_CONTENT_URI,
                null, // projection
                null, // selection
                null, // selection args
                null  // sort order
        );

        TestDb.validateCursor(cursor, TestDb.getFullListValues());
    }


}