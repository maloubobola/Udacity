package com.example.thomasthiebaud.android.movie.http;

import org.json.JSONObject;

/**
 * Created by thiebaudthomas on 18/09/15.
 */
public interface HttpResponse {
    void onSuccess(JSONObject object);

    void onError(Exception exception);
}
