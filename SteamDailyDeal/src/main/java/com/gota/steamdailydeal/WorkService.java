package com.gota.steamdailydeal;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gota.steamdailydeal.constants.StorefrontAPI;

import org.json.JSONObject;

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
        App.queue.add(new JsonObjectRequest(
                Request.Method.GET,
                StorefrontAPI.FEATURED_CATEGORIES,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        processJSONObject(jsonObject);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e(App.TAG, "Error on request JSON!", volleyError);
                    }
                }));
    }

    private void processJSONObject(JSONObject jsonObject) {

    }

}
