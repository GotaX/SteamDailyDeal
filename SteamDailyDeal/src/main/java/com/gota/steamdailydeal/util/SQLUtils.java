package com.gota.steamdailydeal.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;

import com.gota.steamdailydeal.data.DataProvider;
import com.gota.steamdailydeal.data.Tables;
import com.gota.steamdailydeal.entity.Deal;

import java.util.List;

/**
 * Created by Gota on 2014/5/31.
 * Email: G.tianxiang@gmail.com
 */
public class SQLUtils {

    public static final Uri DAIL_DEAL = Uri.withAppendedPath(
            DataProvider.CONTENT_URI, DataProvider.PATH_DAILY_DEAL);
    public static final Uri SPOTLIGHT = Uri.withAppendedPath(
            DataProvider.CONTENT_URI, DataProvider.PATH_SPOTLIGHT);
    public static final Uri WEEK_LONG_DEAL = Uri.withAppendedPath(
            DataProvider.CONTENT_URI, DataProvider.PATH_WEEK_LONG_DEAL);

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
        if (deals == null || deals.isEmpty()) return;

        Uri deal = Uri.withAppendedPath(DataProvider.CONTENT_URI, DataProvider.PATH_DEAL);

        ContentValues[] values = new ContentValues[deals.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = deals.get(i).getContentValues();
        }
        Deal first = deals.get(0);
        if (first.category == Tables.TDeals.CAT_DAILY_DEAL ||
            first.category == Tables.TDeals.CAT_SPOTLIGHT) {
            cr.delete(DAIL_DEAL, null, null);
            cr.delete(SPOTLIGHT, null, null);
        } else if (first.category == Tables.TDeals.CAT_WEEK_LONG_DEAL) {
            cr.delete(WEEK_LONG_DEAL, null, null);
        }
        cr.bulkInsert(deal, values);
    }

}
