package com.gota.steamdailydeal;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.gota.steamdailydeal.constants.Steam;
import com.gota.steamdailydeal.constants.SteamAPI;
import com.gota.steamdailydeal.entity.Deal;
import com.gota.steamdailydeal.util.HTMLUtils;
import com.gota.steamdailydeal.util.JSONUtils;
import com.gota.steamdailydeal.util.NetUtils;
import com.gota.steamdailydeal.util.SQLUtils;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WorkService extends IntentService {

    private static final String ACTION_UPDATE_DAILY_DEAL_AND_SPOTLIGHT =
            "com.gota.steamdailydeal.action.update_daily_deal_and_spotlight";
    private static final String ACTION_WEEK_LONG_DEAL =
            "com.gota.steamdailydeal.action.update_week_long_deal";

    private static final int NOTIFICATION_ID = 0x26;
    private static int sTaskCount = 0;

    private NotificationCompat.Builder mBuilder;
    private NotificationManager mManager;

    public static void startActionUpdateData(Context context) {
        Log.d(App.TAG, "Start service!");
        startAction(context, ACTION_UPDATE_DAILY_DEAL_AND_SPOTLIGHT);
    }

    public static void startActionWeekLongDeal(Context context) {
        Log.d(App.TAG, "Start service!");
        startAction(context, ACTION_WEEK_LONG_DEAL);
    }

    private static void startAction(Context context, String action) {
        // Running task no more than 3
        if (sTaskCount >= 3) return;

        sTaskCount++;
        Intent intent = new Intent(context, WorkService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    public WorkService() {
        super("WorkService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        completeNotification();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (intent != null) {
                final String action = intent.getAction();
                switch (action) {
                    case ACTION_UPDATE_DAILY_DEAL_AND_SPOTLIGHT:
                        handleActionUpdateDailDealAndSpotlight();
                        break;
                    case ACTION_WEEK_LONG_DEAL:
                        handleActionWeekLongDeal();
                        break;
                }
            }
        } finally {
            sTaskCount --;
        }
    }

    private void handleActionUpdateDailDealAndSpotlight() {
        Log.d(App.TAG, "Start handle update daily deal and spotlight action!");

        // Show notification
        String notifyTitle = getString(R.string.notification_title_daily_deal_and_spot_light);
        String notifyContent = getString(R.string.notification_content_request_data);
        initNotification(notifyTitle, notifyContent);

        // Start request data
        RequestFuture<String> future = RequestFuture.newFuture();
        App.queue.add(new StringRequest(
                Request.Method.GET, SteamAPI.FEATURED_CATEGORIES, future, future));
        try {
            // Receive data
            String json = future.get();
            Log.d(App.TAG, "Receive JSON string:");
            if (App.DEBUG) Log.d(App.TAG, json);

            // Parse JSON data
            fireNotification(getString(R.string.notification_content_parse_data));
            List<Deal> deals = JSONUtils.parseDeal(json);
            Log.d(App.TAG, "JSON string parsed!");

            // Save deals to database
            fireNotification(getString(R.string.notification_content_store_data));
            SQLUtils.saveDeals(getContentResolver(), deals);
            Log.d(App.TAG, "Deals from JSON saved to database");

            // Download images to cache
            fireNotification(getString(R.string.notification_content_cache_images));
            downloadDailyDealImagesToCache(deals);
            Log.d(App.TAG, "Download images to cache");

            // Broadcast intent to update UI
            completeNotification();
            updateUI();
            Log.d(App.TAG, "Broadcast update UI");
        } catch (JSONException e) {
            completeNotification(getString(R.string.notification_content_fail_parse_data));
            Log.e(App.TAG, "Error on parse JSON!", e);
        } catch (InterruptedException | ExecutionException e) {
            completeNotification(getString(R.string.notification_content_fail_request_data));
            Log.e(App.TAG, "Error on request JSON!", e);
        }
    }

    private void handleActionWeekLongDeal() {
        Log.d(App.TAG, "Start handle week long deals action!");

        // Show notification
        String notifyTitle = getString(R.string.notification_title_week_long_deals);
        String notifyContent = getString(R.string.notification_content_request_data);
        initNotification(notifyTitle, notifyContent);

        try {
            // Start request data
            Document doc = Jsoup.connect(Steam.WEEK_LONG_DEAL).get();
            Log.d(App.TAG, "Receive HTML Document:");
            if (App.DEBUG) Log.d(App.TAG, doc.html());

            // Parse HTML data
            fireNotification(getString(R.string.notification_content_parse_data));
            List<Deal> deals = HTMLUtils.paresWeekLongDeals(doc);
            Log.d(App.TAG, "HTML parsed!");

            // Save deals to database
            fireNotification(getString(R.string.notification_content_store_data));
            SQLUtils.saveDeals(getContentResolver(), deals);
            Log.d(App.TAG, "Deals from html saved to database");

            // Download images to cache
            fireNotification(getString(R.string.notification_content_cache_images));
            downloadWeekLongDealsImagesToCache(deals);
            Log.d(App.TAG, "Download images to cache");

            // Broadcast intent to update UI
            completeNotification();
            updateUI();
            Log.d(App.TAG, "Broadcast update UI");
        } catch (IOException e) {
            completeNotification(getString(R.string.notification_content_fail_request_data));
            Log.e(App.TAG, "Error on connect to Week Long Deal!", e);
        }
    }

    private void updateUI() {
        Intent broadcast = new Intent(DailyDealWidget.ACTION_UPDATE_UI);
        broadcast.setClass(getApplicationContext(), DailyDealWidget.class);
        sendBroadcast(broadcast);
    }

    private void downloadDailyDealImagesToCache(List<Deal> deals) {
        int count = 0;
        for (Deal deal : deals) {
            String content = getString(R.string.notification_content_cache_images);
            fireNotification(content, deals.size(), count++);
            String url = deal.appInfo.headerImage;
            NetUtils.loadImageToCache(url);
        }
    }

    private void downloadWeekLongDealsImagesToCache(List<Deal> deals) {
        int count = 0;
        for (Deal deal : deals) {
            String content = getString(R.string.notification_content_cache_images);
            fireNotification(content, deals.size(), count++);
            int type = deal.appInfo.type;
            int id = Integer.parseInt(deal.appInfo.id);
            String url = Steam.getMediumPic(type, id);
            NetUtils.loadImageToCache(url);
        }
    }

    // ================== Notification update methods ==================

    private void initNotification(String title, String content) {
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        mBuilder.setProgress(0, 0, true);
        fireNotification();
    }

    private void fireNotification() {
        mManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void fireNotification(String content) {
        mBuilder.setContentText(content);
        fireNotification();
    }

    private void fireNotification(String content, int max, int progress) {
        mBuilder.setContentText(String.format("%s (%s/%s)", content, progress, max));
        mBuilder.setProgress(max, progress, false);
        fireNotification();
    }

    private void completeNotification() {
        mManager.cancel(NOTIFICATION_ID);
    }

    private void completeNotification(String content) {
        mBuilder.setContentText(content);
        mBuilder.setProgress(0, 0, false);
        fireNotification();
    }
}
