package com.gota.steamdailydeal.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.gota.steamdailydeal.util.SQLUtils;

import static com.gota.steamdailydeal.data.Tables.SQL;
import static com.gota.steamdailydeal.data.Tables.TAppInfo;
import static com.gota.steamdailydeal.data.Tables.TDeals;

/**
 * Created by Gota on 2014/5/20.
 * Email: G.tianxiang@gmail.com
 */
public class DataProvider extends ContentProvider {

    public static final String AUTHORITY = "com.gota.steamdailydeal.data.provider";
    public static final String URL = "content://" + AUTHORITY;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final int APP_INFO = 1;
    public static final int APP_INFO_ROW = 2;
    public static final int DAILY_DEAL = 3;
    public static final int WEEK_LONG_DEAL = 4;
    public static final int WEDNESDAY_DEAL = 5;

    public static final String PATH_APP_INFO = "appInfo";
    public static final String PATH_DAILY_DEAL = "dailyDeal";
    public static final String PATH_WEEK_LONG_DEAL = "weekLongDeal";
    public static final String PATH_WEDNESDAY_DEAL = "wednesdayDeal";

    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, PATH_APP_INFO, APP_INFO);
        sUriMatcher.addURI(AUTHORITY, PATH_APP_INFO + "/#", APP_INFO_ROW);
        sUriMatcher.addURI(AUTHORITY, PATH_DAILY_DEAL, DAILY_DEAL);
        sUriMatcher.addURI(AUTHORITY, PATH_WEEK_LONG_DEAL, WEEK_LONG_DEAL);
        sUriMatcher.addURI(AUTHORITY, PATH_WEDNESDAY_DEAL, WEDNESDAY_DEAL);
    }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch(sUriMatcher.match(uri)) {
            case  APP_INFO:
                builder.setTables(TAppInfo.TABLE);
                break;
            case APP_INFO_ROW:
                builder.setTables(TAppInfo.TABLE);
                builder.appendWhere(TAppInfo._ID + "=" + uri.getLastPathSegment());
                break;
            case DAILY_DEAL:
                builder.setTables(SQL.DEALS_JOIN_APP_INFO);
                builder.appendWhere(TDeals.TYPE + "=" + TDeals.TYPE_DAILY_DEAL);
                break;
            case WEEK_LONG_DEAL:
                builder.setTables(SQL.DEALS_JOIN_APP_INFO);
                builder.appendWhere(TDeals.TYPE + "=" + TDeals.TYPE_WEEK_LONG_DEAL);
                break;
            case WEDNESDAY_DEAL:
                builder.setTables(SQL.DEALS_JOIN_APP_INFO);
                builder.appendWhere(TDeals.TYPE + "=" + TDeals.TYPE_WEDNESDAY_DEAL);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Cursor c = builder.query(db, SQL.DEALS_JOIN_APP_INFO_PROJECTION,
                selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case APP_INFO:
                return "vnd.android.cursor.dir/vnd.steam.appInfo";
            case APP_INFO_ROW:
                return "vnd.android.cursor.item/vnd.steam.appInfo";
            case DAILY_DEAL:
                return "vnd.android.cursor.dir/vnd.steam.appInfo";
            case WEEK_LONG_DEAL:
                return "vnd.android.cursor.dir/vnd.steam.appInfo";
            case WEDNESDAY_DEAL:
                return "vnd.android.cursor.dir/vnd.steam.appInfo";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId = 0;

        db.beginTransaction();
        switch (sUriMatcher.match(uri)) {
            case APP_INFO:
                rowId = db.insert(TAppInfo.TABLE, null, values);
                break;
            case DAILY_DEAL:
                values.put(TDeals.TYPE, TDeals.TYPE_DAILY_DEAL);
                rowId = db.insert(TAppInfo.TABLE, null, values);
                db.insert(TDeals.TABLE, null, values);
                break;
            case WEEK_LONG_DEAL:
                values.put(TDeals.TYPE, TDeals.TYPE_WEEK_LONG_DEAL);
                rowId = db.insert(TAppInfo.TABLE, null, values);
                db.insert(TDeals.TABLE, null, values);
                break;
            case WEDNESDAY_DEAL:
                values.put(TDeals.TYPE, TDeals.TYPE_WEDNESDAY_DEAL);
                rowId = db.insert(TAppInfo.TABLE, null, values);
                db.insert(TDeals.TABLE, null, values);
                break;
        }
        db.endTransaction();

        if (rowId > 0) {
            Uri _uri = Uri.withAppendedPath(CONTENT_URI, PATH_APP_INFO);
            _uri = ContentUris.withAppendedId(_uri, rowId);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case APP_INFO:
                return db.delete(TAppInfo.TABLE, null, null);
            case APP_INFO_ROW:
                return db.delete(TAppInfo.TABLE, TAppInfo._ID + "=" + uri.getLastPathSegment(), null);
            case DAILY_DEAL:
                return db.delete(TDeals.TABLE, TDeals.TYPE + "=" + TDeals.TYPE_DAILY_DEAL, null);
            case WEEK_LONG_DEAL:
                return db.delete(TDeals.TABLE, TDeals.TYPE + "=" + TDeals.TYPE_WEEK_LONG_DEAL, null);
            case WEDNESDAY_DEAL:
                return db.delete(TDeals.TABLE, TDeals.TYPE + "=" + TDeals.TYPE_WEDNESDAY_DEAL, null);
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case APP_INFO:
                return db.update(TAppInfo.TABLE, values, selection, selectionArgs);
            case APP_INFO_ROW:
                String where = SQLUtils.createWhere(
                        TAppInfo._ID + "=" + uri.getLastPathSegment(), selection);
                return db.update(TAppInfo.TABLE, values, where, selectionArgs);
            case DAILY_DEAL:
                where = SQLUtils.createWhere(
                        TDeals.TYPE + "=" + TDeals.TYPE_DAILY_DEAL, selection);
                return db.update(TDeals.TABLE, values, where, selectionArgs);
            case WEEK_LONG_DEAL:
                where = SQLUtils.createWhere(
                        TDeals.TYPE + "=" + TDeals.TYPE_WEEK_LONG_DEAL, selection);
                return db.update(TDeals.TABLE, values, where, selectionArgs);
            case WEDNESDAY_DEAL:
                where = SQLUtils.createWhere(
                        TDeals.TYPE + "=" + TDeals.TYPE_WEDNESDAY_DEAL, selection);
                return db.update(TDeals.TABLE, values, where, selectionArgs);
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
