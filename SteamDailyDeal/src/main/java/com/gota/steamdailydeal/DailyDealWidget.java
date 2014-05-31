package com.gota.steamdailydeal;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.gota.steamdailydeal.constants.Steam;
import com.gota.steamdailydeal.data.DataProvider;
import com.gota.steamdailydeal.data.Tables;
import com.gota.steamdailydeal.util.MyTextUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Implementation of App Widget functionality.
 */
public class DailyDealWidget extends AppWidgetProvider {

    public static final String ACTION_REFRESH = "com.gota.dailydeal.action_refresh";

    public static final String KEY_WIDGET_ID = "keyWidgetId";
    public static final String KEY_NEED_RETRY = "keyNeedRetry";
    public static final String KEY_FORCE_REFRESH = "keyForceRefresh";

    enum Size {
        SMALL, MEDIUM, LARGE
    }

    private Map<Integer, Size> mSizeMap = new HashMap<>();

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
            RemoteViews views = buildLargeLayout(context, appWidgetId);
            if (views == null) continue;

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
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

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        int cell = (minHeight + 2) / 72;

        Size screenSize;
        if (cell <= 1) {
            screenSize = Size.SMALL;
        } else if (cell == 2) {
            screenSize = Size.MEDIUM;
        } else {
            screenSize = Size.LARGE;
        }
        mSizeMap.put(appWidgetId, screenSize);

        Log.d(App.TAG, String.format(
                "app_id: %s [minWidth=%s, maxWidth=%s, minHeight=%s, maxHeight=%s]",
                appWidgetId, minWidth, maxWidth, minHeight, maxHeight));
        Log.d(App.TAG, "screen size: " + screenSize);

        RemoteViews views = buildLayout(context, appWidgetManager, appWidgetId);
        if (views != null) {
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private RemoteViews buildSmallLayout(final Context context,
                                         final AppWidgetManager appWidgetManager, final int appWidgetId) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, DataProvider.PATH_DAILY_DEAL);
        Cursor cursor = cr.query(uri, Tables.SQL.PROJECTION_DAILY_DEAL_SMALL, null, null, null);

        if (cursor.moveToFirst()) {
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_small_item);

            int id = cursor.getInt(cursor.getColumnIndex(Tables.TDeals.ID));
            String headerImage = Steam.getMediumPic(id);
            String currency = cursor.getString(cursor.getColumnIndex(Tables.TDeals.CURRENCY));
            CharSequence name = cursor.getString(cursor.getColumnIndex(Tables.TDeals.NAME));
            CharSequence discountPercent = MyTextUtils.getDiscount(
                    cursor.getString(cursor.getColumnIndex(Tables.TDeals.DISCOUNT_PERCENT)));
            int op = cursor.getInt(cursor.getColumnIndex(Tables.TDeals.ORIGINAL_PRICE));
            CharSequence originalPrice = MyTextUtils.strikethrough(MyTextUtils.getCurrency(op, currency));
            int fp = cursor.getInt(cursor.getColumnIndex(Tables.TDeals.FINAL_PRICE));
            CharSequence finalPrice = MyTextUtils.getCurrency(fp, currency);

            App.imgLoader.get(headerImage, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    views.setImageViewBitmap(R.id.img_header, imageContainer.getBitmap());
                    appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
                }
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(App.TAG, "Error on request small image!", volleyError);
                    views.setImageViewResource(R.id.img_header, R.drawable.not_found);
                    appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
                }
            });
            views.setTextViewText(R.id.tv_name, name);
            views.setTextViewText(R.id.tv_discount_percent, discountPercent);
            views.setTextViewText(R.id.tv_original_price, originalPrice);
            views.setTextViewText(R.id.tv_price, finalPrice);

            views.setOnClickPendingIntent(R.id.btn_refresh, createRefreshPendingIntent(context));
            views.setOnClickPendingIntent(R.id.img_header, createHeaderImagePendingIntent(context));
            return views;
        } else {
            // TODO: Return empty view
            return null;
        }
    }

    private RemoteViews buildMediumLayout(Context context, int appWidgetId) {
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
        return views;
    }

    private RemoteViews buildLayout(Context context,
                                    AppWidgetManager appWidgetManager, int appWidgetId) {
        Size screenSize = mSizeMap.get(appWidgetId);
        if (screenSize == null) {
            return null;
        } else switch (screenSize) {
            case SMALL:
                return buildSmallLayout(context, appWidgetManager, appWidgetId);
            case MEDIUM:
                return buildMediumLayout(context, appWidgetId);
            case LARGE:
                return buildLargeLayout(context, appWidgetId);
            default:
                return null;
        }
    }

    private RemoteViews buildLargeLayout(Context context, int appWidgetId) {
        return null;
    }

    private RemoteViews newRemoteViews() {
        return new RemoteViews(App.instance.getPackageName(), R.layout.widget_main);
    }

    private PendingIntent createRefreshPendingIntent(Context context) {
        Intent refreshIntent = new Intent(context, DailyDealWidget.class);
        refreshIntent.setAction(ACTION_REFRESH);
        PendingIntent pi = PendingIntent.getBroadcast(
                context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    private PendingIntent createHeaderImagePendingIntent(Context context) {
        Intent ivf = new Intent(Intent.ACTION_VIEW);
        PendingIntent pi = PendingIntent.getActivity(
                context, 0, ivf, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }
}


