package com.example.thomasthiebaud.android.movie.controller.data.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.model.contract.DatabaseContract;
import com.example.thomasthiebaud.android.movie.model.item.TrailerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomasthiebaud on 04/10/15.
 */
public class TrailerAdapter extends CursorAdapter {

    public TrailerAdapter(Context context, Cursor c, int flags) {
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

        textView.setText(cursor.getString(DatabaseContract.TrailerEntry.COLUMN_NAME_INDEX));
    }

    public List<ContentValues> toContentValuesList(String movieId) {
        List<ContentValues> values = new ArrayList<>();
        for(int i=0; i<getCount(); i++) {
            Cursor cursor = (Cursor) getItem(i);
            ContentValues value = new ContentValues();
            value.put(DatabaseContract.TrailerEntry._ID, cursor.getString(DatabaseContract.TrailerEntry._ID_INDEX));
            value.put(DatabaseContract.TrailerEntry.COLUMN_KEY, cursor.getString(DatabaseContract.TrailerEntry.COLUMN_KEY_INDEX));
            value.put(DatabaseContract.TrailerEntry.COLUMN_NAME, cursor.getString(DatabaseContract.TrailerEntry.COLUMN_NAME_INDEX));
            value.put(DatabaseContract.TrailerEntry.COLUMN_ID_MOVIE, movieId);
            values.add(value);
        }
        return values;
    }
}
