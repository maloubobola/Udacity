package com.example.thomasthiebaud.android.movie.model.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.contract.DatabaseContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomasthiebaud on 04/10/15.
 */
public class ReviewAdapter extends CursorAdapter {

    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.default_list_item_layout,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(R.id.default_list_item);

        if(textView == null)
            textView = new TextView(context);

        textView.setText(cursor.getString(DatabaseContract.ReviewEntry.COLUMN_CONTENT_INDEX));
    }

    public List<ContentValues> toContentValuesList(String movieId) {
        List<ContentValues> values = new ArrayList<>();
        for(int i=0; i<getCount(); i++) {
            Cursor cursor = (Cursor) getItem(i);
            ContentValues value = new ContentValues();
            value.put(DatabaseContract.ReviewEntry._ID, cursor.getString(DatabaseContract.ReviewEntry._ID_INDEX));
            value.put(DatabaseContract.ReviewEntry.COLUMN_AUTHOR, cursor.getString(DatabaseContract.ReviewEntry.COLUMN_AUTHOR_INDEX));
            value.put(DatabaseContract.ReviewEntry.COLUMN_CONTENT, cursor.getString(DatabaseContract.ReviewEntry.COLUMN_CONTENT_INDEX));
            value.put(DatabaseContract.ReviewEntry.COLUMN_URL, cursor.getString(DatabaseContract.ReviewEntry.COLUMN_URL_INDEX));
            value.put(DatabaseContract.ReviewEntry.COLUMN_ID_MOVIE, movieId);
            values.add(value);
        }
        return values;
    }
}
