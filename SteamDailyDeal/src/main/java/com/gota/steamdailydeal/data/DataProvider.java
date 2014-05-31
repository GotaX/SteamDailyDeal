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
import static com.gota.steamdailydeal.data.Tables.TDeals;

/**
 * Created by Gota on 2014/5/20.
 * Email: G.tianxiang@gmail.com
 */
public class DataProvider extends ContentProvider {

    public static final String AUTHORITY = "com.gota.steamdailydeal.data.provider";
    public static final String URL = "content://" + AUTHORITY;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final int DEAL = 1;
    public static final int DEAL_ROW = 2;
    public static final int DAILY_DEAL = 3;
    public static final int SPOTLIGHT = 4;
    public static final int WEEK_LONG_DEAL = 5;

    public static final String PATH_DEAL = "deal";
    public static final String PATH_DAILY_DEAL = "dailyDeal";
    public static final String PATH_SPOTLIGHT = "spotlight";
    public static final String PATH_WEEK_LONG_DEAL = "weekLongDeal";

    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, PATH_DEAL, DEAL);
        sUriMatcher.addURI(AUTHORITY, PATH_DEAL + "/#", DEAL_ROW);
        sUriMatcher.addURI(AUTHORITY, PATH_DAILY_DEAL, DAILY_DEAL);
        sUriMatcher.addURI(AUTHORITY, PATH_SPOTLIGHT, SPOTLIGHT);
        sUriMatcher.addURI(AUTHORITY, PATH_WEEK_LONG_DEAL, WEEK_LONG_DEAL);
    }

    private SQLiteDatabase db;

    public static Uri getUriByPath(String path) {
        return Uri.withAppendedPath(CONTENT_URI, path);
    }

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
        String[] defaultProjection = null;
        switch(sUriMatcher.match(uri)) {
            case DEAL:
                builder.setTables(TDeals.TABLE);
                break;
            case DEAL_ROW:
                builder.setTables(TDeals.TABLE);
                builder.appendWhere(TDeals._ID + " = " + uri.getLastPathSegment());
            case DAILY_DEAL:
                builder.setTables(TDeals.TABLE);
                builder.appendWhere(TDeals.CATEGORY + "=" + TDeals.CAT_DAILY_DEAL);
                defaultProjection = SQL.PROJECTION_DAILY_DEAL;
                break;
            case SPOTLIGHT:
                builder.setTables(TDeals.TABLE);
                builder.appendWhere(TDeals.TYPE + "=" + TDeals.CAT_SPOTLIGHT);
                defaultProjection = SQL.PROJECTION_SPOTLIGHT;
                break;
            case WEEK_LONG_DEAL:
                builder.setTables(TDeals.TABLE);
                builder.appendWhere(TDeals.TYPE + "=" + TDeals.CAT_WEEK_LONG_DEAL);
                defaultProjection = SQL.PROJECTION_WEEK_LONG;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        projection = projection == null ? defaultProjection : projection;
        Cursor c = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case DEAL:
                return "vnd.android.cursor.dir/vnd.steam.appInfo";
            case DEAL_ROW:
                return "vnd.android.cursor.item/vnd.steam.appInfo";
            case DAILY_DEAL:
                return "vnd.android.cursor.dir/vnd.steam.appInfo";
            case SPOTLIGHT:
                return "vnd.android.cursor.dir/vnd.steam.appInfo";
            case WEEK_LONG_DEAL:
                return "vnd.android.cursor.dir/vnd.steam.appInfo";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId = 0;
        switch (sUriMatcher.match(uri)) {
            case DAILY_DEAL:
                rowId = insertDeal(values, TDeals.CAT_DAILY_DEAL);
                break;
            case SPOTLIGHT:
                rowId = insertDeal(values, TDeals.CAT_SPOTLIGHT);
                break;
            case WEEK_LONG_DEAL:
                rowId = insertDeal(values, TDeals.CAT_WEEK_LONG_DEAL);
                break;
        }
        if (rowId > 0) {
            Uri _uri = Uri.withAppendedPath(CONTENT_URI, PATH_DEAL);
            _uri = ContentUris.withAppendedId(_uri, rowId);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        return null;
    }

    private long insertDeal(ContentValues values, int category) {
        values.put(TDeals.CATEGORY, category);
        return db.insert(TDeals.TABLE, null, values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String where;
        switch (sUriMatcher.match(uri)) {
            case DEAL:
                return db.delete(TDeals.TABLE, selection, selectionArgs);
            case DEAL_ROW:
                where = SQLUtils.createWhere(TDeals._ID + "=" + uri.getLastPathSegment(), selection);
                return db.delete(TDeals.TABLE, where, selectionArgs);
            case DAILY_DEAL:
                where = SQLUtils.createWhere(TDeals.CATEGORY + "=" + TDeals.CAT_DAILY_DEAL, selection);
                return db.delete(TDeals.TABLE, where, selectionArgs);
            case SPOTLIGHT:
                where = SQLUtils.createWhere(TDeals.CATEGORY + "=" + TDeals.CAT_SPOTLIGHT, selection);
                return db.delete(TDeals.TABLE, where, selectionArgs);
            case WEEK_LONG_DEAL:
                where = SQLUtils.createWhere(TDeals.CATEGORY + "=" + TDeals.CAT_WEEK_LONG_DEAL, selection);
                return db.delete(TDeals.TABLE, where, selectionArgs);
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String where;
        switch (sUriMatcher.match(uri)) {
            case DEAL:
                return db.update(TDeals.TABLE, values, selection, selectionArgs);
            case DEAL_ROW:
                where = SQLUtils.createWhere(
                        TDeals._ID + "=" + uri.getLastPathSegment(), selection);
                return db.update(TDeals.TABLE, values, where, selectionArgs);
            case DAILY_DEAL:
                where = SQLUtils.createWhere(
                        TDeals.CATEGORY + "=" + TDeals.CAT_DAILY_DEAL, selection);
                return db.update(TDeals.TABLE, values, where, selectionArgs);
            case SPOTLIGHT:
                where = SQLUtils.createWhere(
                        TDeals.CATEGORY + "=" + TDeals.CAT_SPOTLIGHT, selection);
                return db.update(TDeals.TABLE, values, where, selectionArgs);
            case WEEK_LONG_DEAL:
                where = SQLUtils.createWhere(
                        TDeals.CATEGORY + "=" + TDeals.CAT_WEEK_LONG_DEAL, selection);
                return db.update(TDeals.TABLE, values, where, selectionArgs);
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
