package com.example.thomasthiebaud.android.movie.http;

import android.net.Uri;
import android.util.Log;

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

/**
 * Created by thiebaudthomas on 18/09/15.
 */
public class HttpTask {

    private final String TAG = HttpTask.class.getSimpleName();

    private HttpMethod httpMethod = HttpMethod.GET;
    private JSONObject body = new JSONObject();

    private Uri.Builder builder = new Uri.Builder();

    private HttpTask(String authority) {
        builder.authority(authority);
    }

    public static HttpTask authority(String authority) {
        return new HttpTask(authority);
    }

    public HttpTask scheme(String scheme) {
        builder.scheme(scheme);
        return this;
    }

    public HttpTask appendPath(String path) {
        builder.appendPath(path);
        return this;
    }

    public HttpTask appendQuery(String key, String value) {
        builder.appendQueryParameter(key, value);
        return this;
    }

    /**
     * Must be execute outside the UI Thread.
     * @return Response from server.
     */
    public JSONObject execute() throws IOException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        URL url;
        try {
            url = new URL(builder.build().toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(httpMethod.name());
            urlConnection.connect();

            if(httpMethod == HttpMethod.POST) {
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                if (body != null) {
                    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                    out.write(body.toString().getBytes());
                    out.flush ();
                    out.close ();
                }
            }

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null)
                buffer.append(line + "\n");

            if (buffer.length() == 0)
                return null;

            try {
                return new JSONObject(buffer.toString());
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json", e);
                return null;
            }
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
    }
}
