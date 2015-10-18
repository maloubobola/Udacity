package com.example.thomasthiebaud.android.movie.controller.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
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

import com.example.thomasthiebaud.android.movie.controller.data.adapter.ReviewAdapter;
import com.example.thomasthiebaud.android.movie.controller.data.adapter.TrailerAdapter;
import com.example.thomasthiebaud.android.movie.controller.data.loader.DetailCursorLoaderCallback;
import com.example.thomasthiebaud.android.movie.model.contract.DatabaseContract;
import com.example.thomasthiebaud.android.movie.model.contract.LoaderContract;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;
import com.example.thomasthiebaud.android.movie.R;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment /*implements LoaderManager.LoaderCallbacks<Cursor>*/ {
    private final String TAG = DetailFragment.class.getSimpleName();

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private MovieItem item = null;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        reviewAdapter = new ReviewAdapter(rootView.getContext(),null,0);
        trailerAdapter = new TrailerAdapter(rootView.getContext(),null,0);

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

        ListView reviewsList = ((ListView) rootView.findViewById(R.id.reviews_list));
        reviewsList.setAdapter(reviewAdapter);

        ListView trailersList = ((ListView) rootView.findViewById(R.id.trailers_list));
        trailersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor)parent.getItemAtPosition(position);
                if(cursor != null) {
                    String key = cursor.getString(DatabaseContract.TrailerEntry.COLUMN_KEY_INDEX);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key));
                    startActivity(intent);
                }
            }
        });
        trailersList.setAdapter(trailerAdapter);

        DetailCursorLoaderCallback callback = new DetailCursorLoaderCallback(getActivity())
                .setItem(item)
                .setReviewAdapter(reviewAdapter)
                .setTrailerAdapter(trailerAdapter);

        getLoaderManager().initLoader(LoaderContract.ONE_MOVIE_LOADER, null, callback);
        getLoaderManager().initLoader(LoaderContract.ALL_REVIEW_LOADER, null, callback);
        getLoaderManager().initLoader(LoaderContract.ALL_TRAILER_LOADER, null, callback);

        ((ListView)rootView.findViewById(R.id.trailers_list)).setEmptyView(rootView.findViewById(R.id.empty_trailers_label));
        ((ListView)rootView.findViewById(R.id.reviews_list)).setEmptyView(rootView.findViewById(R.id.empty_reviews_label));

        return rootView;
    }
}
