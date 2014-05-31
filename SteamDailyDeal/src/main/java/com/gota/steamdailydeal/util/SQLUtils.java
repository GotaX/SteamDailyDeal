package com.gota.steamdailydeal.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.gota.steamdailydeal.App;
import com.gota.steamdailydeal.data.DataProvider;
import com.gota.steamdailydeal.data.Tables;
import com.gota.steamdailydeal.entity.Deal;

import java.util.List;

/**
 * Created by Gota on 2014/5/31.
 * Email: G.tianxiang@gmail.com
 */
public class SQLUtils {

    public static String createWhere(String where1, String where2) {
        if (TextUtils.isEmpty(where2)) {
            return where1;
        } else {
            return "(" + where1 + ") AND (" + where2 + ")";
        }
    }

    public static Uri getUriByCategory(int category) {
        String strCategory = "";
        switch (category) {
            case Tables.TDeals.CAT_DAILY_DEAL:
                strCategory = DataProvider.PATH_DAILY_DEAL;
                break;
            case Tables.TDeals.CAT_SPOTLIGHT:
                strCategory = DataProvider.PATH_SPOTLIGHT;
                break;
            case Tables.TDeals.CAT_WEEK_LONG_DEAL:
                strCategory = DataProvider.PATH_WEEK_LONG_DEAL;
                break;
        }
        return Uri.withAppendedPath(DataProvider.CONTENT_URI, strCategory);
    }

    public static void saveDeals(ContentResolver cr, List<Deal> deals) {
        for (Deal deal : deals) {
            Log.d(App.TAG, "Save deal: " + deal);
        }

        Uri dailyDeal = Uri.withAppendedPath(DataProvider.CONTENT_URI, DataProvider.PATH_DAILY_DEAL);
        Uri spotlight = Uri.withAppendedPath(DataProvider.CONTENT_URI, DataProvider.PATH_SPOTLIGHT);
        Uri deal = Uri.withAppendedPath(DataProvider.CONTENT_URI, DataProvider.PATH_DEAL);

        ContentValues[] values = new ContentValues[deals.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = deals.get(i).getContentValues();
        }

        cr.delete(dailyDeal, null, null);
        cr.delete(spotlight, null, null);
        cr.bulkInsert(deal, values);
    }

}
