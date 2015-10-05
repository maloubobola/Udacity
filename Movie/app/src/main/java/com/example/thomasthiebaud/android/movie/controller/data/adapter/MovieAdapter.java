package com.example.thomasthiebaud.android.movie.controller.data.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.model.contract.DatabaseContract;
import com.squareup.picasso.Picasso;

/**
 * Created by thomasthiebaud on 02/10/15.
 */
public class MovieAdapter extends CursorAdapter {

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.grid_view_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.grid_item_image);
        if (imageView == null)
            imageView = new ImageView(context);

        imageView.setAdjustViewBounds(true);

        Picasso.with(context)
                .load(cursor.getString(DatabaseContract.MovieEntry.COLUMN_POSTER_PATH_INDEX))
                .placeholder(R.drawable.ic_cached_black)
                .error(R.drawable.ic_report_problem_black)
                .fit()
                .into(imageView);
    }
}
