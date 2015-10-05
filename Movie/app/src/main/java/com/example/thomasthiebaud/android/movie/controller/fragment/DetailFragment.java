package com.example.thomasthiebaud.android.movie.controller.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.thomasthiebaud.android.movie.controller.data.adapter.ReviewAdapter;
import com.example.thomasthiebaud.android.movie.controller.data.adapter.TrailerAdapter;
import com.example.thomasthiebaud.android.movie.controller.data.loader.MovieCursorLoaderCallback;
import com.example.thomasthiebaud.android.movie.controller.data.loader.ReviewCursorLoaderCallback;
import com.example.thomasthiebaud.android.movie.controller.data.loader.TrailerCursorLoaderCallback;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;
import com.example.thomasthiebaud.android.movie.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {
    private final String TAG = DetailFragment.class.getSimpleName();

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private Boolean isFavorite = false;

    public DetailFragment() {}

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        reviewAdapter = new ReviewAdapter(rootView.getContext(),null,0);
        trailerAdapter = new TrailerAdapter(rootView.getContext(),null,0);

        Intent intent = getActivity().getIntent();
        MovieItem item = null;

        Bundle argument = getArguments();

        if(argument != null)
            item = argument.getParcelable(MovieItem.class.getSimpleName());

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

        ListView reviewsList = ((ListView) rootView.findViewById(R.id.reviews_list));
        reviewsList.setAdapter(reviewAdapter);

        ListView trailersList = ((ListView) rootView.findViewById(R.id.trailers_list));
        trailersList.setAdapter(trailerAdapter);

        getLoaderManager().initLoader(ReviewCursorLoaderCallback.ALL_REVIEW_LOADER, null, new ReviewCursorLoaderCallback(getActivity(), reviewAdapter, movieId, "popularity"));

        getLoaderManager().initLoader(TrailerCursorLoaderCallback.ALL_TRAILER_LOADER, null, new TrailerCursorLoaderCallback(getActivity(), trailerAdapter, movieId, "popularity"));

/*
        getLoaderManager().initLoader(MovieCursorLoaderCallback.ONE_MOVIE_LOADER, null, new MovieLoaderCallback(getActivity()).setMovieId(movieId).onResponse(new LoaderResponse<MovieItem>() {
            @Override
            public void onSuccess(List<MovieItem> items) {
                if (!items.isEmpty()) {
                    ((FloatingActionButton) rootView.findViewById(R.id.favorite_button)).setImageResource(R.drawable.ic_favorite_black);
                    isFavorite = true;
                }
            }
        }));
        */

        /*
        final ContentValues movieValues = item.toContentValues();
        final List<ContentValues> reviewValues = new ArrayList<>();
        final List<ContentValues> trailerValues = new ArrayList<>();

        for(ReviewItem r : reviewAdapter.getItems())
            reviewValues.add(r.toContentValues(movieId));

        for(TrailerItem t : trailerAdapter.getItems())
            trailerValues.add(t.toContentValues(movieId));

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
        */

        ((ListView)rootView.findViewById(R.id.trailers_list)).setEmptyView(rootView.findViewById(R.id.empty_trailers_label));
        ((ListView)rootView.findViewById(R.id.reviews_list)).setEmptyView(rootView.findViewById(R.id.empty_reviews_label));

        return rootView;
    }

}
