package com.example.thomasthiebaud.android.movie.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.thomasthiebaud.android.movie.adapter.ReviewAdapter;
import com.example.thomasthiebaud.android.movie.adapter.TrailerAdapter;
import com.example.thomasthiebaud.android.movie.http.HttpResponse;
import com.example.thomasthiebaud.android.movie.http.HttpService;
import com.example.thomasthiebaud.android.movie.model.contract.APIContract;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;
import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.model.item.ReviewItem;
import com.example.thomasthiebaud.android.movie.model.item.TrailerItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private final String TAG = DetailActivityFragment.class.getSimpleName();

    public DetailActivityFragment() {}

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

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
                final TrailerAdapter trailerAdapter = new TrailerAdapter(getContext());
                ListView listView = ((ListView) rootView.findViewById(R.id.trailers_list));
                listView.setAdapter(trailerAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailerAdapter.getItem(position).getKey()));
                        startActivity(intent);
                    }
                });
                trailerAdapter.addAll(getTrailers(object));
            }
        }).execute();

        new HttpService().getReview(item.getId()+"").callback(new HttpResponse() {
            @Override
            public void onResponse(JSONObject object) {
                final ReviewAdapter reviewAdapter = new ReviewAdapter(getContext());
                ListView listView = ((ListView) rootView.findViewById(R.id.reviews_list));
                listView.setAdapter(reviewAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(reviewAdapter.getItem(position).getUrl()));
                        startActivity(intent);
                    }
                });
                reviewAdapter.addAll(getReviews(object));
            }
        }).execute();

        return rootView;
    }

    private List<TrailerItem> getTrailers(JSONObject json) {
        List<TrailerItem> trailers = new ArrayList<>();
        JSONArray results;
        try {
            results = json.getJSONArray(APIContract.JSON_RESULTS);
            for(int i=0; i<results.length(); i++) {
                JSONObject jsonTrailer = results.getJSONObject(i);
                TrailerItem trailerItem = new TrailerItem();
                trailerItem.setId(jsonTrailer.getString(APIContract.JSON_ID));
                trailerItem.setName(jsonTrailer.getString(APIContract.JSON_NAME));
                trailerItem.setKey(jsonTrailer.getString(APIContract.JSON_KEY));
                trailers.add(trailerItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailers;
    }

    private List<ReviewItem> getReviews(JSONObject json) {
        List<ReviewItem> reviews = new ArrayList<>();
        JSONArray results;
        try {
            results = json.getJSONArray(APIContract.JSON_RESULTS);
            for(int i=0; i<results.length(); i++) {
                JSONObject jsonReview = results.getJSONObject(i);
                ReviewItem reviewItem = new ReviewItem();
                reviewItem.setId(jsonReview.getString(APIContract.JSON_ID));
                reviewItem.setAuthor(jsonReview.getString(APIContract.JSON_AUTHOR));
                reviewItem.setContent(jsonReview.getString(APIContract.JSON_CONTENT));
                reviewItem.setUrl(jsonReview.getString((APIContract.JSON_URL)));
                reviews.add(reviewItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }
}
