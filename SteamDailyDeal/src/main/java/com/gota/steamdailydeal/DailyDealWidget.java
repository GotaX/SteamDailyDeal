package com.gota.steamdailydeal;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.gota.steamdailydeal.api.Steam;
import com.gota.steamdailydeal.entity.AppInfo;
import com.gota.steamdailydeal.exception.NoNetworkException;
import com.gota.steamdailydeal.util.MyTextUtils;


/**
 * Implementation of App Widget functionality.
 */
public class DailyDealWidget extends AppWidgetProvider implements RefreshDataTask.UpdateUIListener {

    public static final String ACTION_REFRESH = "com.gota.dailydeal.action_refresh";

    public static final String KEY_WIDGET_ID = "keyWidgetId";
    public static final String KEY_NEED_RETRY = "keyNeedRetry";
    public static final String KEY_FORCE_REFRESH = "keyForceRefresh";

    private Context mContext;
    private AppWidgetManager mAppWidgetManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(App.TAG, "on receive " + intent.getAction());

        this.mContext = context;
        this.mAppWidgetManager = AppWidgetManager.getInstance(context);

        String action = intent.getAction();
        if (ACTION_REFRESH.equals(action)) {
            int widgetId = intent.getIntExtra(KEY_WIDGET_ID, 0);
            boolean needRetry = intent.getBooleanExtra(KEY_NEED_RETRY, false);
            boolean forceRefresh = intent.getBooleanExtra(KEY_FORCE_REFRESH, false);
            Log.d(App.TAG, String.format("receive: needRetry= %s, forceRefresh=%s", needRetry, forceRefresh));
            startUpdate(widgetId, needRetry, forceRefresh);
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(App.TAG, "on update");
        for (int appWidgetId : appWidgetIds) {
            startUpdate(appWidgetId, false, false);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        Log.d(App.TAG, "on deleted");
        App.queue.stop();
        App.instance.cancelAlarm();
        App.prefs.edit().clear().commit();
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
    }

    @Override
    public void onUpdateUI(RemoteViews views, int widgetId, AppInfo app) {
        String discountPercent = String.format("-%s%s", app.discountPercent, "%");
        views.setTextViewText(R.id.tv_discount_percent, discountPercent);

        String originalPrice = String.format("$%s", app.originalPrice / 100f);
        views.setTextViewText(R.id.tv_original_price, MyTextUtils.strikethrough(originalPrice));

        String price = String.format("$%s %s", app.finalPrice / 100f, app.currency);
        views.setTextViewText(R.id.tv_price, price);

        views.setTextViewText(R.id.tv_name, app.name);
        initOnClickUrl(views, app.id);

        views.setImageViewResource(R.id.img_header, R.drawable.loading);
        mAppWidgetManager.updateAppWidget(widgetId, views);

        loadImage(views, widgetId, app.headerImage);
    }

    private void startUpdate(int appWidgetId, boolean needRetry, boolean forceRefresh) {
        RemoteViews views = newRemoteViews();
        startNewTask(views, appWidgetId, needRetry, forceRefresh);
        initViews(views, appWidgetId);
    }

    private RemoteViews newRemoteViews() {
        return new RemoteViews(App.instance.getPackageName(), R.layout.daily_deal_widget);
    }

    private void startNewTask(RemoteViews views, int widgetId,
                              boolean needRetry, boolean forceRefresh) {
        int retryTime = needRetry ? RefreshDataTask.DEFAULT_RETRY_TIME : 0;
        RefreshDataTask task = new RefreshDataTask(retryTime, views, widgetId, forceRefresh, this);
        if (forceRefresh || task.isNeedToUpdate()) {
            try {
                task.start();
            } catch (NoNetworkException e) {
                Toast.makeText(mContext, R.string.no_available_network, Toast.LENGTH_SHORT).show();
                views.setImageViewResource(R.id.img_header, R.drawable.not_found);
            }
        }
    }

    private void initViews(RemoteViews views, int widgetId) {
        initButtons(views, widgetId);
        mAppWidgetManager.updateAppWidget(widgetId, views);
    }

    private void initButtons(RemoteViews views, int appWidgetId) {
        Log.d(App.TAG, "init button.");
        Intent refreshIntent = new Intent(mContext, DailyDealWidget.class);
        refreshIntent.setAction(ACTION_REFRESH);
        refreshIntent.putExtra(KEY_WIDGET_ID, appWidgetId);
        refreshIntent.putExtra(KEY_NEED_RETRY, false);
        refreshIntent.putExtra(KEY_FORCE_REFRESH, true);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(
                mContext, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btn_refresh, refreshPendingIntent);
        mAppWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void initOnClickUrl(RemoteViews views, int appId) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Steam.getStoreLink(appId)));
        PendingIntent pendingIntent = PendingIntent.getActivity(
                mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.img_header, pendingIntent);
    }

    private void loadImage(final RemoteViews views, final int appWidgetId, String url) {
        Log.d(App.TAG, "Start load image!");
        App.imgLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                Log.d(App.TAG, "Load image success!");
                views.setImageViewBitmap(R.id.img_header, imageContainer.getBitmap());
                mAppWidgetManager.updateAppWidget(appWidgetId, views);
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(App.TAG, "Load image error!");
                views.setImageViewResource(R.id.img_header, R.drawable.not_found);
                mAppWidgetManager.updateAppWidget(appWidgetId, views);
            }
        });
        App.queue.start();
    }

}


