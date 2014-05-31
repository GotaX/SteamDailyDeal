package com.gota.steamdailydeal.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gota.steamdailydeal.App;
import com.gota.steamdailydeal.data.Tables;
import com.gota.steamdailydeal.entity.AppInfo;
import com.gota.steamdailydeal.entity.Deal;

import org.json.JSONException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gota on 2014/5/31.
 * Email: G.tianxiang@gmail.com
 */
public class JSONUtils {

    public static List<Deal> parseDeal(String json) throws JSONException {
        JsonParser parser = new JsonParser();
        JsonElement root = parser.parse(json);
        return processJson(root);
    }

    public static List<Deal> parseDeal(InputStream in) throws JSONException {
        JsonParser parser = new JsonParser();
        JsonElement root = parser.parse(new InputStreamReader(in));
        return processJson(root);
    }

    private static List<Deal> processJson(JsonElement root) throws JSONException {
        ArrayList<Deal> deals = new ArrayList<>();
        JsonObject rootObj = root.getAsJsonObject();
        for (int i = 0; rootObj.has(String.valueOf(i)); i++) {
            JsonObject item = rootObj.getAsJsonObject(String.valueOf(i));
            Deal deal = processDealItem(item);
            deals.add(deal);
        }
        return deals;
    }

    private static Deal processDealItem(JsonObject obj) throws JSONException {
        Deal deal = new Deal();

        String category = obj.get("id").getAsString();
        switch (category) {
            case "cat_spotlight":
                deal.category = Tables.TDeals.CAT_SPOTLIGHT;
                break;
            case "cat_dailydeal":
                deal.category = Tables.TDeals.CAT_DAILY_DEAL;
                break;
            default:
                throw new IllegalArgumentException("Unknown category: " + category);
        }

        JsonElement item = obj.getAsJsonArray("items").get(0);
        deal.appInfo = App.gson.fromJson(item, AppInfo.class);
        return deal;
    }
}
