package com.gota.steamdailydeal;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gota.steamdailydeal.constants.Pref;
import com.gota.steamdailydeal.entity.FeaturedCategories;
import com.gota.steamdailydeal.gson.FeaturedCategoriesDeserializer;
import com.gota.steamdailydeal.volley.BitmapCache;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Gota on 2014/5/18.
 * Email: G.tianxiang@gmail.com
 */
public class App extends Application {
    public static final String TAG = "MiniSteam";
    public static final int INTERVAL = 24 * 3600 * 1000;
    public static final boolean DEBUG = false;

    public static RequestQueue queue;
    public static ImageLoader imgLoader;
    public static BitmapCache cache;
    public static App instance;
    public static Gson gson;
    public static SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        queue = Volley.newRequestQueue(this);
        cache = new BitmapCache();
        imgLoader = new ImageLoader(queue, cache);

        gson = new GsonBuilder()
                .registerTypeAdapter(FeaturedCategories.class, new FeaturedCategoriesDeserializer())
                .create();
    }

    public Calendar getUpdateCalendar() {
        Calendar calendar = Calendar.getInstance();

        int hourOfDay = prefs.getInt(Pref.REFRESH_HOUER, 1);
        int minute = prefs.getInt(Pref.REFRESH_MINITUE, 0);

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            int oneDayInMillisecond = (int) TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
            calendar.add(Calendar.MILLISECOND, oneDayInMillisecond);
        }
        return calendar;
    }

    public void setupAlarm() {
        Calendar calendar = getUpdateCalendar();
        long time = calendar.getTimeInMillis();

        PendingIntent pendingIntent = createRefreshPendingIntent();

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC, time, INTERVAL, pendingIntent);

        Log.d(TAG, "setup alarm: " + calendar);
    }

    public void cancelAlarm() {
        PendingIntent pendingIntent = createRefreshPendingIntent();
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);

        Log.d(TAG, "cancel alarm.");
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    private PendingIntent createRefreshPendingIntent() {
        Intent updateIntent = new Intent(this, DailyDealWidget.class);
        updateIntent.setAction(DailyDealWidget.ACTION_REFRESH);
        updateIntent.putExtra("alarm", true);
        return PendingIntent.getBroadcast(this, 1, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
