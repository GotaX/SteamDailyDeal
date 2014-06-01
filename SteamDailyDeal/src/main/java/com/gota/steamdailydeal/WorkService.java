package com.gota.steamdailydeal;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gota.steamdailydeal.constants.Steam;
import com.gota.steamdailydeal.constants.SteamAPI;
import com.gota.steamdailydeal.entity.Deal;
import com.gota.steamdailydeal.util.HTMLUtils;
import com.gota.steamdailydeal.util.JSONUtils;
import com.gota.steamdailydeal.util.SQLUtils;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public class WorkService extends IntentService {

    private static final String ACTION_UPDATE_DATA = "com.gota.steamdailydeal.action.UPDATE_DATA";
    private static final String ACTION_WEEK_LONG_DEAL = "com.gota.steamdailydeal.action.grab_data_week_long_deal";

    public static void startActionUpdateData(Context context) {
        Log.d(App.TAG, "Start service!");
        Intent intent = new Intent(context, WorkService.class);
        intent.setAction(ACTION_UPDATE_DATA);
        context.startService(intent);
    }

    public static void startActionWeekLongDeal(Context context) {
        Log.d(App.TAG, "Start service!");
        Intent intent = new Intent(context, WorkService.class);
        intent.setAction(ACTION_WEEK_LONG_DEAL);
        context.startService(intent);
    }

    public WorkService() {
        super("WorkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_UPDATE_DATA:
                    handleActionUpdateData();
                    break;
                case ACTION_WEEK_LONG_DEAL:
                    handleActionWeekLongDeal();
                    break;
            }
        }
    }

    private void handleActionUpdateData() {
        Log.d(App.TAG, "Handle update data action!");
        App.queue.add(new StringRequest(
                Request.Method.GET,
                SteamAPI.FEATURED_CATEGORIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.d(App.TAG, "Receive JSON string:\n" + s);
                        try {
                            List<Deal> deals = JSONUtils.parseDeal(s);
                            Log.d(App.TAG, "JSON string parsed!");
                            SQLUtils.saveDeals(getContentResolver(), deals);
                            Log.d(App.TAG, "Deals from JSON saved to database");
                            updateUI();
                            Log.d(App.TAG, "Broadcast update UI");
                        } catch (JSONException e) {
                            Log.e(App.TAG, "Error on parse JSON!", e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e(App.TAG, "Error on request JSON!", volleyError);
                    }
                }));
    }

    private void handleActionWeekLongDeal() {
        try {
            Document doc = Jsoup.connect(Steam.WEEK_LONG_DEAL).get();
            Log.d(App.TAG, "Receive HTML Document:\n" + doc.html());
            List<Deal> deals = HTMLUtils.paresWeekLongDeals(doc);
            Log.d(App.TAG, "HTML parsed!");
            SQLUtils.saveDeals(getContentResolver(), deals);
            Log.d(App.TAG, "Deals from html saved to database");
            updateUI();
            Log.d(App.TAG, "Broadcast update UI");
        } catch (IOException e) {
            Log.e(App.TAG, "Error on connect to Week Long Deal!", e);
        }
    }

    private void updateUI() {
        Intent broadcast = new Intent(DailyDealWidget.ACTION_UPDATE_UI);
        broadcast.setClass(getApplicationContext(), DailyDealWidget.class);
        sendBroadcast(broadcast);
    }
}
