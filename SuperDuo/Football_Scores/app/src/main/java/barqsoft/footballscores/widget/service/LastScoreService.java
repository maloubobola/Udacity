package barqsoft.footballscores.widget.service;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import barqsoft.footballscores.R;
import barqsoft.footballscores.app.main.MainActivity;
import barqsoft.footballscores.widget.provider.LastScoreProvider;
import barqsoft.footballscores.contract.DatabaseContract;

/**
 * Created by thomasthiebaud on 08/12/15.
 */
public class LastScoreService extends IntentService {
    private static final String TAG = LastScoreService.class.getSimpleName();

    public LastScoreService() {
        super(LastScoreService.class.getSimpleName());
    }

    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
        // Prior to Jelly Bean, widgets were always their default size
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return getResources().getDimensionPixelSize(R.dimen.widget_last_score_default_width);
        }
        // For Jelly Bean and higher devices, widgets can be resized - the current size can be
        // retrieved from the newly added App Widget Options
        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return  getResources().getDimensionPixelSize(R.dimen.widget_last_score_default_width);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        //views.setContentDescription(R.id.widget_icon, description);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, LastScoreProvider.class));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar cal = Calendar.getInstance();

        String currentDate = dateFormat.format(cal.getTime());

        Cursor data = context.getContentResolver().query(
                DatabaseContract.BASE_CONTENT_URI,
                null,
                DatabaseContract.ScoresTable.DATE_COL + " <= ?",
                new String[]{currentDate},
                DatabaseContract.ScoresTable.DATE_COL + " DESC"
        );

        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        String home_name = data.getString(DatabaseContract.ScoresTable.INDEX_HOME);
        String away_name = data.getString(DatabaseContract.ScoresTable.INDEX_AWAY);
        String home_score = data.getString(DatabaseContract.ScoresTable.INDEX_HOME_GOALS);
        String away_score = data.getString(DatabaseContract.ScoresTable.INDEX_AWAY_GOALS);

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            int widgetWidth = getWidgetWidth(appWidgetManager, appWidgetId);
            int largeWidth = getResources().getDimensionPixelSize(R.dimen.widget_last_score_large_width);
            int layoutId;
            if (widgetWidth >= largeWidth) {
                layoutId = R.layout.widget_last_score_large;
            } else {
                layoutId = R.layout.widget_last_score;
            }

            RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);

            // Content Descriptions for RemoteViews were only added in ICS MR1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                setRemoteContentDescription(views, "description");
            }

            views.setTextViewText(R.id.home_name, home_name);
            views.setTextViewText(R.id.away_name, away_name);
            views.setTextViewText(R.id.home_score, home_score);
            views.setTextViewText(R.id.away_score, away_score);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
