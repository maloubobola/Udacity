package barqsoft.footballscores.app.score;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import barqsoft.footballscores.R;
import barqsoft.footballscores.app.main.MainActivity;
import barqsoft.footballscores.service.FetchService;
import barqsoft.footballscores.service.LastScoreWidgetService;

/**
 * Created by thomasthiebaud on 07/12/15.
 */
public class LastScoreWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, LastScoreWidgetService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, LastScoreWidgetService.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (FetchService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            context.startService(new Intent(context, LastScoreWidgetService.class));
        }
    }
}
