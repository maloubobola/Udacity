package com.example.thomasthiebaud.android.movie.http;

import org.json.JSONObject;

/**
 * Created by thomasthiebaud on 26/09/15.
 */
public interface JsonResponse {
    void onSuccess(JSONObject object);
    void onError(Exception exception);
}
