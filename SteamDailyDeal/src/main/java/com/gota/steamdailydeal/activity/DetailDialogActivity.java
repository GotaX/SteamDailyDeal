package com.gota.steamdailydeal.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gota.steamdailydeal.App;
import com.gota.steamdailydeal.R;
import com.gota.steamdailydeal.constants.Steam;
import com.gota.steamdailydeal.constants.SteamAPI;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailDialogActivity extends Activity {

    public static final String KEY_APP_ID = "app_id";
    public static final String KEY_APP_TYPE = "app_type";
    public static final String KEY_APP_NAME = "app_name";

    private int mAppType;
    private int mAppId;
    private String mAppName;

    View vEmpty;
    View vContent;

    TextView tvName;
    TextView tvCurrentLowestPrice;
    TextView tvHistoryLowestPrice;
    TextView tvBundleCount;
    ImageView btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_dialog);

        Intent intent = getIntent();
        mAppId = intent.getIntExtra(KEY_APP_ID, 0);
        mAppType = intent.getIntExtra(KEY_APP_TYPE, 0);
        mAppName = intent.getStringExtra(KEY_APP_NAME);
        Log.d(App.TAG, "id=" + mAppId + ", type=" + mAppType);

        vEmpty = findViewById(android.R.id.empty);
        vContent = findViewById(R.id.content);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvCurrentLowestPrice = (TextView) findViewById(R.id.tv_current_lowest_price);
        tvHistoryLowestPrice = (TextView) findViewById(R.id.tv_history_lowest_price);
        tvBundleCount = (TextView) findViewById(R.id.tv_bundle_count);
        btnHome = (ImageView) findViewById(R.id.btn_home);

        tvName.setText(mAppName);

        requestData();
    }

    private void requestData() {
        Log.d(App.TAG, "Start request JSON");
        String query = String.format(SteamAPI.LOWEST_PRICE, Steam.getTypeStr(mAppType), mAppId);
        App.queue.add(new JsonObjectRequest(
            Request.Method.GET,
            query, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(App.TAG, "Response: " + response);
                    try {
                        processJSON(response);
                    } catch (JSONException e) {
                        Toast.makeText(DetailDialogActivity.this, R.string.notification_content_fail_parse_data, Toast.LENGTH_LONG).show();
                    }
                    completeRequest();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(App.TAG, "Error on reqeust lowest price!", error);
                    Toast.makeText(DetailDialogActivity.this, R.string.notification_content_fail_request_data, Toast.LENGTH_LONG).show();
                    completeRequest();
                }
            }));
    }

    private void processJSON(JSONObject json) throws JSONException {
        // Parse JSON data
        JSONObject objPrice = json.getJSONObject("price");
        String priceStore = objPrice.getString("store");
        String priceCut = objPrice.getString("cut");
        String pricePrice = objPrice.getString("price");
        String priceUrl = objPrice.getString("url");
        CharSequence price = Html.fromHtml(String.format(
                "$%s (-%s%s) at <a href=\"%s\">%s</a>",
                pricePrice, "%", priceCut, priceUrl, priceStore));

        JSONObject objLowest = json.getJSONObject("lowest");
        String lowestStore = objLowest.getString("store");
        String lowestCut = objLowest.getString("cut");
        String lowestPrice = objLowest.getString("price");
        String lowestUrl = objLowest.getString("url");
        String lowestRecorderFormatted = objLowest.getString("recorded_formatted");
        CharSequence lowest = Html.fromHtml(String.format(
                "$%s (-%s%s) at <a href=\"%s\">%s</a> %s",
                lowestPrice, "%", lowestCut, lowestUrl, lowestStore, lowestRecorderFormatted
        ));

        JSONObject objBundles = json.getJSONObject("bundles");
        int count = objBundles.getInt("count");
        String bundlesCount = String.format("%s 次", count);

        // Setup UI
        tvCurrentLowestPrice.setText(price);
        tvHistoryLowestPrice.setText(lowest);
        tvBundleCount.setText(bundlesCount);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String storeLink = Steam.getStoreLink(mAppType, mAppId);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(storeLink));
                startActivity(intent);
            }
        });
    }

    private void completeRequest() {
        vEmpty.setVisibility(View.GONE);
        vContent.setVisibility(View.VISIBLE);
    }
}
