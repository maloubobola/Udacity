package com.example.thomasthiebaud.android.movie.controller.data.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thiebaudthomas on 09/09/15.
 */
public class MovieAdapter extends BaseAdapter {
    private final String TAG = MovieAdapter.class.getSimpleName();
    private final Context context;
    private List<MovieItem> movies = new ArrayList<>();

    public MovieAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public MovieItem getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view = null;
        if (view == null) {
            view = new ImageView(context);
            view.setAdjustViewBounds(true);
        } else {
            view = (ImageView) convertView;
        }

        MovieItem movie = getItem(position);
        Picasso.with(context)
                .load(movie.getPosterPath())
                .placeholder(R.drawable.ic_cached_black)
                .error(R.drawable.ic_report_problem_black)
                .fit()
                .into(view);

        return view;
    }

    public void clear() {
        movies.clear();
    }

    public void addAll(List<MovieItem> movies) {
        this.movies = movies;
        this.notifyDataSetChanged();
    }
}
