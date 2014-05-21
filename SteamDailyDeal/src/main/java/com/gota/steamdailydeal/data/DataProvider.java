package com.gota.steamdailydeal.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import com.gota.steamdailydeal.App;
import com.gota.steamdailydeal.entity.AppInfo;
import com.gota.steamdailydeal.entity.CategoryInfo;
import com.gota.steamdailydeal.entity.FeaturedCategories;

import java.util.ArrayList;
import java.util.List;

import static com.gota.steamdailydeal.data.Tables.TAppInfo;
import static com.gota.steamdailydeal.data.Tables.TFeatured;

/**
 * Created by Gota on 2014/5/20.
 * Email: G.tianxiang@gmail.com
 */
public class DataProvider extends ContentProvider {

    public static final Uri CONTENT_URI =
            Uri.parse("content://com.gota.steamdailydeal.data.provider");

    public static final String KEY_JSON = "json";
    public static final String[] COLUMNS = {
            TFeatured.ID, TFeatured.NAME, TAppInfo.ID, TAppInfo.TYPE, TAppInfo.DISCOUNTED,
            TAppInfo.CURRENCY, TAppInfo.ORIGINAL_PRICE, TAppInfo.FINAL_PRICE, TAppInfo.DISCOUNT_PERCENT,
            TAppInfo.NAME, TAppInfo.HEADER_IMAGE, TAppInfo.PURCHASE_PACKAGE, TAppInfo.BODY, TAppInfo.URL
    };

    private static final List<CategoryInfo> mCategoryInfos = new ArrayList<>();

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        for (CategoryInfo categoryInfo : mCategoryInfos) {
            AppInfo appInfo = categoryInfo.items.get(0);
            cursor.addRow(new Object[] {
                    categoryInfo.id, categoryInfo.name, appInfo.id, appInfo.type, appInfo.discounted,
                    appInfo.currency, appInfo.originalPrice, appInfo.finalPrice, appInfo.discountPercent,
                    appInfo.name, appInfo.headerImage, appInfo.purchasePackage, appInfo.body, appInfo.url
            });
        }
        cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        try {
            Log.d(App.TAG, "Update json info!");
            String json = values.getAsString(KEY_JSON);
            FeaturedCategories fc = App.gson.fromJson(json, FeaturedCategories.class);
            mCategoryInfos.clear();
            mCategoryInfos.addAll(fc.map.values());
            Log.d(App.TAG, "Category info size: " + mCategoryInfos.size());
            return mCategoryInfos.size();
        } finally {
            Log.d(App.TAG, "Notify change!");
            getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        }
    }
}
