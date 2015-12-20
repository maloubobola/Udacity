package it.jaschke.alexandria.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import it.jaschke.alexandria.app.main.MainActivity;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.commons.Network;
import it.jaschke.alexandria.contract.APIContract;
import it.jaschke.alexandria.contract.DatabaseContract;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class BookService extends IntentService {

    private final String LOG_TAG = BookService.class.getSimpleName();

    public BookService() {
        super("Alexandria");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(!Network.isAvailable(getApplicationContext())) {
            Intent messageIntent = new Intent(MainActivity.MESSAGE_EVENT);
            messageIntent.putExtra(MainActivity.MESSAGE_KEY, getString(R.string.no_network_error));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
            return;
        }
        if (intent != null) {
            final String action = intent.getAction();
            if (APIContract.FETCH_BOOK.equals(action)) {
                final String ean = intent.getStringExtra(APIContract.EAN);
                fetchBook(ean);
            } else if (APIContract.DELETE_BOOK.equals(action)) {
                final String ean = intent.getStringExtra(APIContract.EAN);
                deleteBook(ean);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void deleteBook(String ean) {
        if(ean!=null) {
            getContentResolver().delete(DatabaseContract.BookEntry.buildBookUri(Long.parseLong(ean)), null, null);
        }
    }

    /**
     * Handle action fetchBook in the provided background thread with the provided
     * parameters.
     *
     * When the url was wrong (eg https://www.googleapis.coms/v1 for example), a null response was returned and the app crashed.
     * Now this case is handle and a "internal server error" is raised.
     */
    private void fetchBook(String ean) {
        if(ean.length() != 13){
            return;
        }

        Cursor bookEntry = getContentResolver().query(
                DatabaseContract.BookEntry.buildBookUri(Long.parseLong(ean)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        if(bookEntry.getCount()>0){
            bookEntry.close();
            return;
        }

        bookEntry.close();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJsonString = null;

        try {
            Uri builtUri = Uri.parse(APIContract.FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(APIContract.QUERY_PARAM, APIContract.ISBN_PARAM + ean)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0) {
                return;
            }
            bookJsonString = buffer.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            if(bookJsonString == null) {
                Intent messageIntent = new Intent(MainActivity.MESSAGE_EVENT);
                messageIntent.putExtra(MainActivity.MESSAGE_KEY,getResources().getString(R.string.internal_server_error));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
                return;
            }

            JSONObject bookJson = new JSONObject(bookJsonString);
            JSONArray bookArray;

            if(bookJson.has(APIContract.ITEMS)) {
                bookArray = bookJson.getJSONArray(APIContract.ITEMS);
            } else {
                Intent messageIntent = new Intent(MainActivity.MESSAGE_EVENT);
                messageIntent.putExtra(MainActivity.MESSAGE_KEY,getResources().getString(R.string.not_found));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
                return;
            }

            JSONObject bookInfo = ((JSONObject) bookArray.get(0)).getJSONObject(APIContract.VOLUME_INFO);

            String title = bookInfo.getString(APIContract.TITLE);

            String subtitle = "";
            if(bookInfo.has(APIContract.SUBTITLE)) {
                subtitle = bookInfo.getString(APIContract.SUBTITLE);
            }

            String desc="";
            if(bookInfo.has(APIContract.DESC)){
                desc = bookInfo.getString(APIContract.DESC);
            }

            String imgUrl = "";
            if(bookInfo.has(APIContract.IMG_URL_PATH) && bookInfo.getJSONObject(APIContract.IMG_URL_PATH).has(APIContract.IMG_URL)) {
                imgUrl = bookInfo.getJSONObject(APIContract.IMG_URL_PATH).getString(APIContract.IMG_URL);
            }

            writeBackBook(ean, title, subtitle, desc, imgUrl);

            if(bookInfo.has(APIContract.AUTHORS)) {
                writeBackAuthors(ean, bookInfo.getJSONArray(APIContract.AUTHORS));
            }
            if(bookInfo.has(APIContract.CATEGORIES)){
                writeBackCategories(ean,bookInfo.getJSONArray(APIContract.CATEGORIES) );
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
        }
    }

    private void writeBackBook(String ean, String title, String subtitle, String desc, String imgUrl) {
        ContentValues values= new ContentValues();
        values.put(DatabaseContract.BookEntry._ID, ean);
        values.put(DatabaseContract.BookEntry.TITLE, title);
        values.put(DatabaseContract.BookEntry.IMAGE_URL, imgUrl);
        values.put(DatabaseContract.BookEntry.SUBTITLE, subtitle);
        values.put(DatabaseContract.BookEntry.DESC, desc);
        getContentResolver().insert(DatabaseContract.BookEntry.CONTENT_URI,values);
    }

    private void writeBackAuthors(String ean, JSONArray jsonArray) throws JSONException {
        ContentValues values= new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(DatabaseContract.AuthorEntry._ID, ean);
            values.put(DatabaseContract.AuthorEntry.AUTHOR, jsonArray.getString(i));
            getContentResolver().insert(DatabaseContract.AuthorEntry.CONTENT_URI, values);
            values= new ContentValues();
        }
    }

    private void writeBackCategories(String ean, JSONArray jsonArray) throws JSONException {
        ContentValues values= new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(DatabaseContract.CategoryEntry._ID, ean);
            values.put(DatabaseContract.CategoryEntry.CATEGORY, jsonArray.getString(i));
            getContentResolver().insert(DatabaseContract.CategoryEntry.CONTENT_URI, values);
            values= new ContentValues();
        }
    }
 }