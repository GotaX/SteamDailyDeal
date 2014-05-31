package com.gota.steamdailydeal;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Arrays;


/**
 * Implementation of App Widget functionality.
 */
public class DailyDealWidget extends AppWidgetProvider {

    public static final String ACTION_REFRESH = "com.gota.dailydeal.action_refresh";

    public static final String KEY_WIDGET_ID = "keyWidgetId";
    public static final String KEY_NEED_RETRY = "keyNeedRetry";
    public static final String KEY_FORCE_REFRESH = "keyForceRefresh";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_REFRESH.equals(action)) {
            WorkService.startActionUpdateData(context);
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(App.TAG, "on update: " + Arrays.toString(appWidgetIds));
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = newRemoteViews();
            Intent intent = new Intent(context, MyWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.vf_main, intent);

            views.setEmptyView(R.id.vf_main, R.id.empty);

            // Init refresh button
            Intent refreshIntent = new Intent(context, DailyDealWidget.class);
            refreshIntent.setAction(ACTION_REFRESH);
            PendingIntent piRefresh = PendingIntent.getBroadcast(
                    context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.btn_refresh, piRefresh);

            // Init onclick image
            Intent ivf = new Intent(Intent.ACTION_VIEW);
            PendingIntent pivf = PendingIntent.getActivity(
                    context, 0, ivf, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.vf_main, pivf);

            // update widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        Log.d(App.TAG, "on deleted");
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d(App.TAG, "on enabled");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.d(App.TAG, "on disabled");
        App.queue.stop();
        App.instance.cancelAlarm();
        App.prefs.edit().clear().commit();
    }

    private RemoteViews newRemoteViews() {
        return new RemoteViews(App.instance.getPackageName(), R.layout.widget_main);
    }

}


