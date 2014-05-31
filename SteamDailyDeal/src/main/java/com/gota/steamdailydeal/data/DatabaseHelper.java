package com.gota.steamdailydeal.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.gota.steamdailydeal.data.Tables.TDeals;

/**
 * Created by Gota on 2014/5/28.
 * Email: G.tianxiang@gmail.com
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "steam.db";
    public static final int DB_VERSION = 1;

    public static final String CREATE_DEALS =
            "create table " + TDeals.TABLE+ " (" +
            TDeals._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TDeals.ID + " INTEGER NOT NULL UNIQUE, " +
            TDeals.TYPE + " INTEGER, " +
            TDeals.NAME + " TEXT, " +
            TDeals.DISCOUNTED + " BOOLEAN, " +
            TDeals.DISCOUNT_PERCENT + " INTEGER, " +
            TDeals.ORIGINAL_PRICE + " INTEGER, " +
            TDeals.FINAL_PRICE + " INTEGER, " +
            TDeals.CURRENCY + " TEXT, " +
            TDeals.LARGE_CAPSULE_IMAGE + " TEXT, " +
            TDeals.SMALL_CAPSULE_IMAGE + " TEXT, " +
            TDeals.DISCOUNT_EXPIRATION + " INTEGER, " +
            TDeals.HEADLINE + " TEXT, " +
            TDeals.CONTROLLER_SUPPORT + " TEXT, " +
            TDeals.PURCHASE_PACKAGE + " TEXT, " +
            TDeals.HEADER_IMAGE + " TEXT, " +
            TDeals.BODY + " TEXT, " +
            TDeals.URL + " TEXT, " +
            TDeals.CATEGORY + " INTEGER " +
            ");";

    public static final String DROP_DEALS = "DROP TABLE IF EXISTS " + TDeals.TABLE;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DEALS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_DEALS);
        onCreate(db);
    }
}
