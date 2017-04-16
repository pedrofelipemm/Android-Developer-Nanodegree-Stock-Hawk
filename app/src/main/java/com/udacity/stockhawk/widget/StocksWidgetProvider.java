package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;

public class StocksWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            //TODO: change to details..
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    //TODO: change to details..
                    .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stocks);
            views.setRemoteAdapter(R.id.widget_list, new Intent(context, StocksWidgetRemoteViewsService.class));
            views.setOnClickPendingIntent(R.id.widget_stocks, clickPendingIntent);
            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }


        Log.d("PEDRO", this.getClass().getSimpleName() + " onUpdate");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (QuoteSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }

        Log.d("PEDRO", this.getClass().getSimpleName() + " onReceive");
    }
}
