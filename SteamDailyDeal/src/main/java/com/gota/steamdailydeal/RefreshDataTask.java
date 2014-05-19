package com.gota.steamdailydeal;

import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gota.steamdailydeal.constants.Pref;
import com.gota.steamdailydeal.constants.StorefrontAPI;
import com.gota.steamdailydeal.entity.AppInfo;
import com.gota.steamdailydeal.entity.FeaturedCategories;
import com.gota.steamdailydeal.exception.NoNetworkException;
import com.gota.steamdailydeal.volley.GsonRequest;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Gota on 2014/5/18.
 * Email: G.tianxiang@gmail.com
 */
public class RefreshDataTask {

    public static final int DEFAULT_RETRY_TIME = 5;

    private UpdateUIListener mListener;

    private int mRetryCount;
    private int mRetryTime;
    private RemoteViews mViews;
    private int mWidgetId;
    private boolean mForceRefresh;

    public RefreshDataTask(int retryTime, RemoteViews views, int widgetId,
                           boolean forceRefresh, UpdateUIListener listener) {
        this.mViews = views;
        this.mWidgetId = widgetId;
        this.mListener = listener;
        this.mForceRefresh = forceRefresh;
        this.mRetryTime = retryTime;
    }

    public void start() throws NoNetworkException {
        if (!App.instance.isNetworkAvailable()) {
            throw new NoNetworkException();
        }
        requestData();
    }

    private void requestData() {
        App.queue.add(new GsonRequest<>(
                Request.Method.GET,
                FeaturedCategories.class,
                StorefrontAPI.FEATURED_CATEGORIES,
                null,
                new Response.Listener<FeaturedCategories>() {
                    @Override
                    public void onResponse(FeaturedCategories featuredCategories) {
                        AppInfo app = featuredCategories.getDailyDeal().items.get(0);
                        if (updateRefreshInfo(app) || mForceRefresh) {
                            mListener.onUpdateUI(mViews, mWidgetId, app);
                        } else if (mRetryCount < mRetryTime) {
                            Log.d(App.TAG, "Retry " + mRetryCount);
                            try {
                                int sleepTime = mRetryCount * 2 + 1;
                                mRetryCount++;
                                TimeUnit.MINUTES.sleep(sleepTime);
                                requestData();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(App.TAG, "", volleyError);
            }
        }));
        App.queue.start();
    }

    public boolean isNeedToUpdate() {
        long lastUpdateTime = App.prefs.getLong(Pref.LAST_UPDATE_TIME, 0);

        Calendar calender = App.instance.getUpdateCalendar();

        Date theDate = calender.getTime();
        Date lastUpdateDate = new Date(lastUpdateTime);
        Date currentDate = new Date(System.currentTimeMillis());

        return lastUpdateDate.before(theDate) && currentDate.after(theDate);
    }

    private boolean updateRefreshInfo(AppInfo app) {
        int lastAppId = App.prefs.getInt(Pref.LAST_APP_ID, 0);
        if (app.id != lastAppId) {
            long lastUpdateTime = System.currentTimeMillis();
            App.prefs.edit()
                    .putInt(Pref.LAST_APP_ID, app.id)
                    .putLong(Pref.LAST_UPDATE_TIME, lastUpdateTime)
                    .commit();
            Log.d(App.TAG, "last update time: " + lastUpdateTime);
            return true;
        }
        return false;
    }

    public interface UpdateUIListener {
        void onUpdateUI(RemoteViews views, int widgetId, AppInfo app);
    }
}
