package com.gota.steamdailydeal;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.gota.steamdailydeal.ui.LayoutBuilder;

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
        LayoutBuilder lb = new LayoutBuilder(context, appWidgetManager);
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = lb.buildMediumLayout(appWidgetId);
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

        LayoutBuilder layoutBuilder = new LayoutBuilder(context, appWidgetManager);
        RemoteViews views = buildLayout(layoutBuilder, appWidgetId);
        if (views != null) {
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private RemoteViews buildLayout(LayoutBuilder layoutBuilder, int appWidgetId) {
        Size screenSize = mSizeMap.get(appWidgetId);
        Log.d(App.TAG, "Build " + screenSize + " layout!");
        if (screenSize == null) {
            return null;
        } else switch (screenSize) {
            case SMALL:
                return layoutBuilder.buildSmallLayout(appWidgetId);
            case MEDIUM:
                return layoutBuilder.buildMediumLayout(appWidgetId);
            case LARGE:
                return layoutBuilder.buildLargeLayout(appWidgetId);
            default:
                return null;
        }
    }

}


