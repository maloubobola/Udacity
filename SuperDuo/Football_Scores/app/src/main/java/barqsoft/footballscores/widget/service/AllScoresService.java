package barqsoft.footballscores.widget.service;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import barqsoft.footballscores.R;
import barqsoft.footballscores.app.main.MainActivity;
import barqsoft.footballscores.commons.Day;
import barqsoft.footballscores.commons.Utils;
import barqsoft.footballscores.contract.BundleContract;
import barqsoft.footballscores.contract.DatabaseContract;

/**
 * Created by thomasthiebaud on 10/12/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AllScoresService extends RemoteViewsService {
    private static final String TAG = AllScoresService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(
                        DatabaseContract.ScoresTable.buildScoreWithDate(),
                        null,
                        null,
                        new String[]{Day.getCurrentStart() + "", Day.getCurrentEnd() + ""},
                        DatabaseContract.ScoresTable.DATE_COL + " DESC"
                );
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_score_item);

                views.setTextViewText(R.id.home_name, data.getString(DatabaseContract.ScoresTable.INDEX_HOME));
                views.setTextViewText(R.id.away_name, data.getString(DatabaseContract.ScoresTable.INDEX_AWAY));

                long timeStamp = data.getLong(DatabaseContract.ScoresTable.INDEX_DATE);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timeStamp);

                views.setTextViewText(R.id.data_textview, calendar.get(Calendar.HOUR_OF_DAY) + ":" + String.format("%02d", calendar.get(Calendar.MINUTE)));
                views.setTextViewText(R.id.score_textview, Utils.getScores(data.getInt(DatabaseContract.ScoresTable.INDEX_HOME_GOALS), data.getInt(DatabaseContract.ScoresTable.INDEX_AWAY_GOALS)));
                //views.matchId = data.getDouble(DatabaseContract.ScoresTable.INDEX_ID);
                views.setImageViewResource(R.id.home_crest, Utils.getTeamCrestByTeamName(data.getString(DatabaseContract.ScoresTable.INDEX_HOME)));
                views.setImageViewResource(R.id.away_crest, Utils.getTeamCrestByTeamName(data.getString(DatabaseContract.ScoresTable.INDEX_AWAY)));


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, "description");
                }


                Bundle bundle = new Bundle();
                bundle.putInt(BundleContract.MATCH_ID, data.getInt(DatabaseContract.ScoresTable.INDEX_MATCH_ID));
                bundle.putInt(BundleContract.MATCH_POSITION, position);

                Intent fillInIntent = new Intent();
                fillInIntent.putExtras(bundle);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.scores_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(0);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                //views.setContentDescription(R.id.widget_icon, description);
            }
        };
    }
}
