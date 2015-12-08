package barqsoft.footballscores.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.R;
import barqsoft.footballscores.app.main.MainActivity;
import barqsoft.footballscores.app.score.LastScoreWidget;
import barqsoft.footballscores.contract.DatabaseContract;

/**
 * Created by thomasthiebaud on 08/12/15.
 */
public class LastScoreWidgetService extends IntentService {
    private static final String TAG = LastScoreWidgetService.class.getSimpleName();


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public LastScoreWidgetService() {
        super("LastScoreWidgetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, LastScoreWidget.class));

        Cursor data = context.getContentResolver().query(
                DatabaseContract.BASE_CONTENT_URI,
                null,
                null,
                null,
                DatabaseContract.ScoresTable.DATE_COL + " DESC"
        );

        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        String home = data.getString(3);

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_last_score_small;
            RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);

            // Add the data to the RemoteViews

            // Content Descriptions for RemoteViews were only added in ICS MR1
            /*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                setRemoteContentDescription(views, description);
            }
            */
            views.setTextViewText(R.id.widget_test_value, home);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
