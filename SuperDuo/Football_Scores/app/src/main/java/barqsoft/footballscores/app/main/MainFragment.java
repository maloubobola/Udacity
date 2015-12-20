package barqsoft.footballscores.app.main;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import barqsoft.footballscores.contract.BundleContract;
import barqsoft.footballscores.contract.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.contract.LoaderContract;
import barqsoft.footballscores.model.adapter.ScoresAdapter;
import barqsoft.footballscores.model.holder.ViewHolder;
import barqsoft.footballscores.service.FetchService;

/**
 * A placeholder fragment containing a simple view.
 */
public final class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = MainFragment.class.getSimpleName();
    private ScoresAdapter scoresAdapter;
    private long startOfDay = 0;
    private long endOfDay = 0;

    private ListView scoreList = null;

    public MainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            startOfDay = savedInstanceState.getLong(BundleContract.START_OF_DAY);
            endOfDay = savedInstanceState.getLong(BundleContract.END_OF_DAY);
        }

        this.updateScores();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        scoreList = (ListView) rootView.findViewById(R.id.scores_list);
        scoresAdapter = new ScoresAdapter(getActivity(),null,0);
        scoreList.setAdapter(scoresAdapter);
        getLoaderManager().initLoader(LoaderContract.SCORES_LOADER, null, this);
        scoresAdapter.setDetailMatchId(MainActivity.selectedMatchId);

        scoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder selected = (ViewHolder) view.getTag();
                scoresAdapter.setDetailMatchId(selected.matchId);
                MainActivity.selectedMatchId = (int) selected.matchId;
                scoresAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), DatabaseContract.ScoresTable.buildScoreWithDate(), null, null, new String[]{startOfDay + "", endOfDay + ""}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            i++;
            cursor.moveToNext();
        }
        scoresAdapter.swapCursor(cursor);
        scoreList.smoothScrollToPosition(MainActivity.matchPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        scoresAdapter.swapCursor(null);
    }

    private void updateScores() {
        Intent service_start = new Intent(getActivity(), FetchService.class);
        getActivity().startService(service_start);
    }

    public void setStartOfDay(long startOfDay) {
        this.startOfDay = startOfDay;
    }

    public void setEndOfDay(long endOfDay) {
        this.endOfDay = endOfDay;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(BundleContract.START_OF_DAY, startOfDay);
        outState.putLong(BundleContract.END_OF_DAY, endOfDay);
        super.onSaveInstanceState(outState);
    }
}
