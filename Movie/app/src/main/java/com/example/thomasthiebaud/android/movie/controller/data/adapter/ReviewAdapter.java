package com.example.thomasthiebaud.android.movie.controller.data.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.model.item.ReviewItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thiebaudthomas on 21/09/15.
 */
public class ReviewAdapter extends BaseAdapter {

    private final Context context;
    private List<ReviewItem> reviews = new ArrayList<>();

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public ReviewItem getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = null;
        if (convertView == null) {
            view = new TextView(context);
        } else {
            view = (TextView) convertView;
        }

        ReviewItem reviewItem = getItem(position);
        view.setText(reviewItem.getContent());

        int padding = (int) context.getResources().getDimension(R.dimen.default_padding);
        view.setPadding(padding, padding, padding, padding);

        return view;
    }

    public void clear() {
        reviews.clear();
    }

    public void addAll(List<ReviewItem> reviews) {
        this.reviews = reviews;
        this.notifyDataSetChanged();
    }

    public List<ReviewItem> getItems() {
        return reviews;
    }
}
