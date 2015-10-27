package com.example.thomasthiebaud.android.movie.commons.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

/**
 * Created by thiebaudthomas on 21/10/15.
 */
public class NoNetworkSafeCursorLoader extends CursorLoader {
    private NoNetworkException exception;

    public NoNetworkSafeCursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Cursor loadInBackground() {
        try {
            return super.loadInBackground();
        } catch(NoNetworkException exception) {
            this.exception = exception;
        }
        return null;
    }

    public NoNetworkException getException() {
        return exception;
    }
}
