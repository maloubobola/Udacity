package com.example.thomasthiebaud.android.movie.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
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
import com.example.thomasthiebaud.android.movie.model.loader.LoaderResponse;
import com.example.thomasthiebaud.android.movie.model.loader.MovieLoader;
import com.example.thomasthiebaud.android.movie.model.loader.ReviewLoader;
import com.example.thomasthiebaud.android.movie.model.loader.TrailerLoader;
import com.example.thomasthiebaud.android.movie.model.contract.APIContract;
import com.example.thomasthiebaud.android.movie.model.contract.DatabaseContract;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;
import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.model.item.ReviewItem;
import com.example.thomasthiebaud.android.movie.model.item.TrailerItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private final String TAG = DetailActivityFragment.class.getSimpleName();

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private boolean isFavorite = false;

    public DetailActivityFragment() {}

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        reviewAdapter = new ReviewAdapter(getContext());
        trailerAdapter = new TrailerAdapter(getContext());

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
                .placeholder(R.drawable.ic_cached_black)
                .error(R.drawable.ic_report_problem_black)
                .into(imageView);

        //Use navbar to display title
        getActivity().setTitle(item.getTitle());

        ((TextView)rootView.findViewById(R.id.overview)).setText(item.getOverview());
        ((TextView)rootView.findViewById(R.id.date)).setText(item.getReleaseDate());
        ((RatingBar)rootView.findViewById(R.id.ratingBar)).setRating((float) item.getVoteAverage());

        final int movieId = item.getId();

        new HttpService().getTrailers(item.getId() + "").onResponse(new HttpResponse() {
            @Override
            public void onSuccess(JSONObject object) {
                trailerAdapter = new TrailerAdapter(getContext());
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

            @Override
            public void onError(Exception exception) {
                if (exception instanceof IOException)
                    getLoaderManager().initLoader(TrailerLoader.TRAILER_LOADER_ID, null, new TrailerLoader(getActivity(), trailerAdapter, movieId));
            }
        }).execute();

        new HttpService().getReview(item.getId() + "").onResponse(new HttpResponse() {
            @Override
            public void onSuccess(JSONObject object) {
                reviewAdapter = new ReviewAdapter(getContext());
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

            @Override
            public void onError(Exception exception) {
                if (exception instanceof IOException)
                    getLoaderManager().initLoader(ReviewLoader.REVIEW_LOADER_ID, null, new ReviewLoader(getActivity(), reviewAdapter, movieId));
            }
        }).execute();

        getLoaderManager().initLoader(MovieLoader.ONE_MOVIE_LOADER, null, new MovieLoader(getActivity()).setMovieId(movieId).onResponse(new LoaderResponse<MovieItem>() {
            @Override
            public void onSuccess(List<MovieItem> items) {
                if (!items.isEmpty()) {
                    ((FloatingActionButton) rootView.findViewById(R.id.favorite_button)).setImageResource(R.drawable.ic_favorite_black);
                    isFavorite = true;
                }
            }
        }));

        final ContentValues movieValues = new ContentValues();
        movieValues.put(DatabaseContract.MovieEntry._ID,item.getId());
        movieValues.put(DatabaseContract.MovieEntry.COLUMN_OVERVIEW,item.getOverview());
        movieValues.put(DatabaseContract.MovieEntry.COLUMN_POSTER_PATH,item.getPosterPath());
        movieValues.put(DatabaseContract.MovieEntry.COLUMN_RELEASE_DATE,item.getReleaseDate());
        movieValues.put(DatabaseContract.MovieEntry.COLUMN_TITLE, item.getTitle());
        movieValues.put(DatabaseContract.MovieEntry.COLUMN_VOTE_AVERAGE, item.getVoteAverage());

        final List<ContentValues> reviewValues = new ArrayList<>();
        final List<ContentValues> trailerValues = new ArrayList<>();

        for(ReviewItem r : reviewAdapter.getItems()) {
            ContentValues value = new ContentValues();
            value.put(DatabaseContract.ReviewEntry._ID, r.getId());
            value.put(DatabaseContract.ReviewEntry.COLUMN_AUTHOR, r.getAuthor());
            value.put(DatabaseContract.ReviewEntry.COLUMN_CONTENT, r.getContent());
            value.put(DatabaseContract.ReviewEntry.COLUMN_URL, r.getUrl());
            value.put(DatabaseContract.ReviewEntry.COLUMN_ID_MOVIE, movieId);
            reviewValues.add(value);
        }

        for(TrailerItem t : trailerAdapter.getItems()) {
            ContentValues value = new ContentValues();
            value.put(DatabaseContract.TrailerEntry._ID, t.getId());
            value.put(DatabaseContract.TrailerEntry.COLUMN_NAME, t.getName());
            value.put(DatabaseContract.TrailerEntry.COLUMN_KEY, t.getKey());
            value.put(DatabaseContract.TrailerEntry.COLUMN_ID_MOVIE, movieId);
            trailerValues.add(value);
        }

        rootView.findViewById(R.id.favorite_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    getContext().getContentResolver().delete(
                            DatabaseContract.ReviewEntry.CONTENT_URI,
                            DatabaseContract.ReviewEntry.COLUMN_ID_MOVIE + " = ?",
                            new String[]{movieId + ""});
                    getContext().getContentResolver().delete(
                            DatabaseContract.TrailerEntry.CONTENT_URI,
                            DatabaseContract.TrailerEntry.COLUMN_ID_MOVIE + " = ?",
                            new String[]{movieId + ""});
                    getContext().getContentResolver().delete(
                            DatabaseContract.MovieEntry.CONTENT_URI,
                            DatabaseContract.MovieEntry._ID + " = ?",
                            new String[]{movieId + ""});
                    ((FloatingActionButton) rootView.findViewById(R.id.favorite_button)).setImageResource(R.drawable.ic_favorite_border_black);
                    isFavorite = false;
                } else {
                    getContext().getContentResolver().insert(
                            DatabaseContract.MovieEntry.CONTENT_URI,
                            movieValues);
                    getContext().getContentResolver().bulkInsert(
                            DatabaseContract.ReviewEntry.CONTENT_URI,
                            reviewValues.toArray(new ContentValues[reviewValues.size()])
                    );
                    getContext().getContentResolver().bulkInsert(
                            DatabaseContract.TrailerEntry.CONTENT_URI,
                            trailerValues.toArray(new ContentValues[trailerValues.size()])
                    );
                    ((FloatingActionButton) rootView.findViewById(R.id.favorite_button)).setImageResource(R.drawable.ic_favorite_black);
                    isFavorite = true;
                }
            }
        });

        ((ListView)rootView.findViewById(R.id.trailers_list)).setEmptyView(rootView.findViewById(R.id.empty_trailers_label));
        ((ListView)rootView.findViewById(R.id.reviews_list)).setEmptyView(rootView.findViewById(R.id.empty_reviews_label));

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
