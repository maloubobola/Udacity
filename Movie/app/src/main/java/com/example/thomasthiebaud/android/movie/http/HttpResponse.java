package com.example.thomasthiebaud.android.movie.http;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by thiebaudthomas on 18/09/15.
 */
public interface HttpResponse<T> {
    void onSuccess(List<T> object);

    List<T> fromJson(JSONObject json);
}
