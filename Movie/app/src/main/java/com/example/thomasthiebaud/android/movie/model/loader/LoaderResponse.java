package com.example.thomasthiebaud.android.movie.model.loader;

import java.util.List;

/**
 * Created by thiebaudthomas on 24/09/15.
 */
public interface LoaderResponse<T> {
    void onSuccess(List<T> items);
}
