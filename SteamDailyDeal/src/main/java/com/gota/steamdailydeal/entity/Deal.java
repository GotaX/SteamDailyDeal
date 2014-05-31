package com.gota.steamdailydeal.entity;

import android.content.ContentValues;

import com.gota.steamdailydeal.data.Tables;

/**
 * Created by Gota on 2014/5/31.
 * Email: G.tianxiang@gmail.com
 */
public class Deal {

    public int category;
    public AppInfo appInfo;

    @Override
    public String toString() {
        return "Deal{" +
                "category=" + category +
                ", appInfo=" + appInfo +
                '}';
    }

    public ContentValues getContentValues() {
        ContentValues values = appInfo.getContentValues();
        values.put(Tables.TDeals.CATEGORY, category);
        return values;
    }
}
