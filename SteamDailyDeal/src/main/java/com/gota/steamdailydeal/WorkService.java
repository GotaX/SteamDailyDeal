package com.gota.steamdailydeal;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gota.steamdailydeal.constants.StorefrontAPI;
import com.gota.steamdailydeal.entity.Deal;
import com.gota.steamdailydeal.util.JSONUtils;
import com.gota.steamdailydeal.util.SQLUtils;

import org.json.JSONException;

import java.util.List;

public class WorkService extends IntentService {

    private static final String ACTION_UPDATE_DATA = "com.gota.steamdailydeal.action.UPDATE_DATA";

    public static void startActionUpdateData(Context context) {
        Intent intent = new Intent(context, WorkService.class);
        intent.setAction(ACTION_UPDATE_DATA);
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
            }
        }
    }

    private void handleActionUpdateData() {
        App.queue.add(new StringRequest(
                Request.Method.GET,
                StorefrontAPI.FEATURED_CATEGORIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            List<Deal> deals = JSONUtils.parseDeal(s);
                            SQLUtils.saveDeals(getContentResolver(), deals);
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

}
