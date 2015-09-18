package com.example.thomasthiebaud.android.movie.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.thomasthiebaud.android.movie.http.HttpResponse;
import com.example.thomasthiebaud.android.movie.http.HttpService;
import com.example.thomasthiebaud.android.movie.model.MovieItem;
import com.example.thomasthiebaud.android.movie.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private final String TAG = DetailActivityFragment.class.getSimpleName();

    public DetailActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        MovieItem item = null;

        if (intent != null && intent.hasExtra(MovieItem.class.getSimpleName()))
            if(intent.getSerializableExtra(MovieItem.class.getSimpleName()) instanceof MovieItem)
                item = (MovieItem) intent.getSerializableExtra(MovieItem.class.getSimpleName());

        if(item == null)
            return rootView;

        ImageView imageView = (ImageView) rootView.findViewById(R.id.poster);

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            imageView.setAdjustViewBounds(true);

        Picasso.with(getContext())
                .load(item.getPosterPath())
                .into(imageView);

        //Use navbar to display title
        getActivity().setTitle(item.getTitle());

        ((TextView)rootView.findViewById(R.id.overview)).setText(item.getOverview());
        ((TextView)rootView.findViewById(R.id.date)).setText(item.getReleaseDate());
        ((RatingBar)rootView.findViewById(R.id.ratingBar)).setRating((float) item.getVoteAverage());

        new HttpService().getVideos(item.getId()+"").callback(new HttpResponse() {
            @Override
            public void onResponse(JSONObject object) {
                Log.e(TAG,object.toString());
            }
        }).execute();

        new HttpService().getReview(item.getId()+"").callback(new HttpResponse() {
            @Override
            public void onResponse(JSONObject object) {
                Log.e(TAG,object.toString());
            }
        }).execute();

        return rootView;
    }
}
