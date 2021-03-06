package com.gota.steamdailydeal;

import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.gota.steamdailydeal.constants.Pref;
import com.gota.steamdailydeal.ui.LayoutBuilder;
import com.gota.steamdailydeal.util.UIUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of App Widget functionality.
 */
public class DailyDealWidget extends AppWidgetProvider {

    public static final String ACTION_REFRESH = "com.gota.dailydeal.action_refresh";
    public static final String ACTION_UPDATE_UI = "com.gota.dailydeal.action_update_ui";

    public static final String KEY_WIDGET_ID = "keyWidgetId";

    public static enum Size {
        SMALL, MEDIUM, LARGE
    }

    private static Map<Integer, Size> sSizeMap = new HashMap<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(App.TAG, "Receive broadcast: " + action);

        switch (action) {
            case ACTION_REFRESH:
                WorkService.startActionUpdateData(context);
                WorkService.startActionWeekLongDeal(context);
                break;
            case ACTION_UPDATE_UI:
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName componentName = new ComponentName(context, DailyDealWidget.class);
                int[] ids = appWidgetManager.getAppWidgetIds(componentName);
                updateUI(context, ids, appWidgetManager);
                break;
            default:
                super.onReceive(context, intent);
                break;
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(App.TAG, "on update: " + Arrays.toString(appWidgetIds));
        updateUI(context, appWidgetIds, appWidgetManager);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d(App.TAG, "on enabled");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        Log.d(App.TAG, "on deleted");
        SharedPreferences.Editor editor = App.prefs.edit();
        for (int id : appWidgetIds) {
            sSizeMap.remove(id);
            editor.remove(Pref.getSizeKey(id));
        }
        editor.commit();
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.d(App.TAG, "on disabled");
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(WorkService.NOTIFICATION_ID);
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

        if (App.DEBUG) {
            Log.d(App.TAG, String.format(
                "app_id: %s [minWidth=%s, maxWidth=%s, minHeight=%s, maxHeight=%s]",
                appWidgetId, minWidth, maxWidth, minHeight, maxHeight));
        }

        Size oldSize = sSizeMap.get(appWidgetId);
        Size screenSize = UIUtils.calculateSize(minHeight);
        if (oldSize == screenSize) return;

        sSizeMap.put(appWidgetId, screenSize);
        App.prefs.edit().putInt(Pref.getSizeKey(appWidgetId), screenSize.ordinal()).commit();
        updateUI(context, new int[]{appWidgetId}, appWidgetManager);
    }

    private void updateUI(Context context, int[] ids, AppWidgetManager appWidgetManager) {
        LayoutBuilder lb = new LayoutBuilder(context);
        for (int id : ids) {
            if (!sSizeMap.containsKey(id)) {
                int ordinal = App.prefs.getInt(Pref.getSizeKey(id), Size.SMALL.ordinal());
                sSizeMap.put(id, Size.values()[ordinal]);
            }
            RemoteViews views = buildLayout(lb, id);
            appWidgetManager.updateAppWidget(id, views);
        }
    }

    private RemoteViews buildLayout(LayoutBuilder layoutBuilder, int appWidgetId) {
        Size screenSize = sSizeMap.get(appWidgetId);
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


