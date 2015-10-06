package com.example.thomasthiebaud.android.movie.controller.data.loader;

/**
 * Created by thomasthiebaud on 06/10/15.
 */
public interface OnResponseCallback<T> {
    void onResponse(T object);
}
