package com.example.thomasthiebaud.android.movie.controller.data.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.model.item.TrailerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thiebaudthomas on 21/09/15.
 */
public class TrailerAdapter extends BaseAdapter {

    private final Context context;
    private List<TrailerItem> trailers = new ArrayList<>();

    public TrailerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return trailers.size();
    }

    @Override
    public TrailerItem getItem(int position) {
        return trailers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = null;
        if (view == null) {
            view = new TextView(context);
        }else {
            view = (TextView) convertView;
        }

        TrailerItem trailerItem = getItem(position);
        view.setText(trailerItem.getName());

        int padding = (int) context.getResources().getDimension(R.dimen.default_padding);
        view.setPadding(padding, padding, padding, padding);

        return view;
    }

    public void clear() {
        trailers.clear();
    }

    public void addAll(List<TrailerItem> movies) {
        this.trailers = movies;
        this.notifyDataSetChanged();
    }

    public List<TrailerItem> getItems() {
        return trailers;
    }
}
