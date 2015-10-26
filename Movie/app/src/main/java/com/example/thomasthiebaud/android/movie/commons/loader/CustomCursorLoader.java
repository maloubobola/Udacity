package com.example.thomasthiebaud.android.movie.commons.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

/**
 * Created by thiebaudthomas on 21/10/15.
 */
public class CustomCursorLoader extends CursorLoader {
    private Throwable throwable;

    public CustomCursorLoader(Context context) {
        super(context);
    }

    public CustomCursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Cursor loadInBackground() {
        try {
            return super.loadInBackground();
        } catch(Throwable t) {
            this.throwable = t;
        }
        return null;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
