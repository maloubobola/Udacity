package com.example.thomasthiebaud.android.movie.controller.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thomasthiebaud.android.movie.controller.data.adapter.ReviewAdapter;
import com.example.thomasthiebaud.android.movie.controller.data.adapter.TrailerAdapter;
import com.example.thomasthiebaud.android.movie.controller.data.loader.DetailCursorLoaderCallback;
import com.example.thomasthiebaud.android.movie.model.contract.BundleContract;
import com.example.thomasthiebaud.android.movie.model.contract.DatabaseContract;
import com.example.thomasthiebaud.android.movie.model.contract.LoaderContract;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;
import com.example.thomasthiebaud.android.movie.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment /*implements LoaderManager.LoaderCallbacks<Cursor>*/ {
    private final String TAG = DetailFragment.class.getSimpleName();

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private MovieItem item = null;
    private ShareActionProvider shareActionProvider;
    private boolean isTwoPane = false;
    private boolean shareVisible = false;

    private MenuItem menuItem;

    private DetailCursorLoaderCallback detailCursorLoaderCallback;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    public void setMenuVisibility(boolean visible) {
        menuItem.setVisible(visible);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        reviewAdapter = new ReviewAdapter(rootView.getContext(),null,0);
        trailerAdapter = new TrailerAdapter(rootView.getContext(),null,0);

        Bundle argument = getArguments();

        if(argument != null) {
            item = argument.getParcelable(MovieItem.class.getSimpleName());
            isTwoPane = argument.getBoolean(BundleContract.IS_TWO_PANE);
            shareVisible = argument.getBoolean(BundleContract.SHARE_VISIBLE);
        }

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
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    String key = cursor.getString(DatabaseContract.TrailerEntry.COLUMN_KEY_INDEX);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key));
                    startActivity(intent);
                }
            }
        });
        trailersList.setAdapter(trailerAdapter);

        detailCursorLoaderCallback = new DetailCursorLoaderCallback(this);

        String sortBy = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity));
        Bundle args = new Bundle();
        args.putString(BundleContract.SORT_BY,sortBy);

        getLoaderManager().initLoader(LoaderContract.ONE_MOVIE_LOADER, args, detailCursorLoaderCallback);
        getLoaderManager().initLoader(LoaderContract.ALL_REVIEW_LOADER, args, detailCursorLoaderCallback);
        getLoaderManager().initLoader(LoaderContract.ALL_TRAILER_LOADER, args, detailCursorLoaderCallback);

        ((ListView)rootView.findViewById(R.id.trailers_list)).setEmptyView(rootView.findViewById(R.id.empty_trailers_label));
        ((ListView)rootView.findViewById(R.id.reviews_list)).setEmptyView(rootView.findViewById(R.id.empty_reviews_label));

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        menuItem = menu.findItem(R.id.action_share);

        if(!isTwoPane)
            menuItem.setVisible(true);
        else
            menuItem.setVisible(shareVisible);

        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }

    public void createShareActionProvider(String content) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");

        String text = content == null ? "No trailer to share !" : "You should check this : http://www.youtube.com/watch?v=" + content;
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        if(shareActionProvider != null)
            shareActionProvider.setShareIntent(shareIntent);
    }

    public TrailerAdapter getTrailerAdapter() {
        return trailerAdapter;
    }

    public ReviewAdapter getReviewAdapter() {
        return reviewAdapter;
    }

    public MovieItem getItem() {
        return item;
    }

    public boolean isTwoPane() {
        return isTwoPane;
    }
}
