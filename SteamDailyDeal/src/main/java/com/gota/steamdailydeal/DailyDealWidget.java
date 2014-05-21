package com.gota.steamdailydeal;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gota.steamdailydeal.constants.StorefrontAPI;
import com.gota.steamdailydeal.data.DataProvider;

import java.util.Arrays;


/**
 * Implementation of App Widget functionality.
 */
public class DailyDealWidget extends AppWidgetProvider {

    public static final String ACTION_REFRESH = "com.gota.dailydeal.action_refresh";

    public static final String KEY_WIDGET_ID = "keyWidgetId";
    public static final String KEY_NEED_RETRY = "keyNeedRetry";
    public static final String KEY_FORCE_REFRESH = "keyForceRefresh";

    private static DataProviderObserver sDataObserver;
    private static HandlerThread sWorkerThread;
    private static Handler sWorkerQueue;

    public DailyDealWidget() {
        sWorkerThread = new HandlerThread("DailyDealWidget-worker");
        sWorkerThread.start();
        sWorkerQueue = new Handler(sWorkerThread.getLooper());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_REFRESH.equals(action)) {
            loadData(context);
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
        App.queue.stop();
        App.instance.cancelAlarm();
        App.prefs.edit().clear().commit();
        unregisterObserver(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d(App.TAG, "on enabled");
        registerObserver(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.d(App.TAG, "on disabled");
        unregisterObserver(context);
    }

    private RemoteViews newRemoteViews() {
        return new RemoteViews(App.instance.getPackageName(), R.layout.widget_main);
    }

    private void loadData(final Context context) {
        App.queue.add(new StringRequest(
                Request.Method.GET,
                StorefrontAPI.FEATURED_CATEGORIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.d(App.TAG, "JSON: " + s);
                        ContentValues values = new ContentValues();
                        values.put(DataProvider.KEY_JSON, s);
                        ContentResolver cr = context.getContentResolver();
                        cr.update(DataProvider.CONTENT_URI, values, null, null);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(App.TAG, "Error on request json!", volleyError);
            }
        }));
        App.queue.start();
    }

    private void registerObserver(Context context) {
        ContentResolver cr = context.getContentResolver();
        if (sDataObserver == null) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, DataProvider.class);
            sDataObserver = new DataProviderObserver(mgr, cn, sWorkerQueue);
            cr.registerContentObserver(DataProvider.CONTENT_URI, true, sDataObserver);
            Log.d(App.TAG, "Register observer!");
        }
    }

    private void unregisterObserver(Context context) {
        if (sDataObserver != null) {
            context.getContentResolver().unregisterContentObserver(sDataObserver);
            sDataObserver = null;
            Log.d(App.TAG, "Unregister observer!");
        }
    }

    class DataProviderObserver extends ContentObserver {
        private AppWidgetManager mAppWidgetManager;
        private ComponentName mComponentName;

        DataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
            super(h);
            mAppWidgetManager = mgr;
            mComponentName = cn;
        }

        @Override
        public void onChange(boolean selfChange) {
            // The data has changed, so notify the widget that the collection view needs to be updated.
            // In response, the factory's onDataSetChanged() will be called which will requery the
            // cursor for the new data.
            Log.d(App.TAG, "Notify app widget view data changed!");
            mAppWidgetManager.notifyAppWidgetViewDataChanged(
                    mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.vf_main);
        }
    }
}


