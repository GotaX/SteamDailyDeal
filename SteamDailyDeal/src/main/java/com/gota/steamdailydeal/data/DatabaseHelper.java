package com.gota.steamdailydeal.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.gota.steamdailydeal.data.Tables.TAppInfo;
import static com.gota.steamdailydeal.data.Tables.TDeals;

/**
 * Created by Gota on 2014/5/28.
 * Email: G.tianxiang@gmail.com
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "steam.db";
    public static final int DB_VERSION = 1;

    public static final String CREATE_APP_INFO =
            "create table " + TAppInfo.TABLE + " (" +
            TAppInfo._ID + " INT PRIMARY KEY AUTOINCREMENT, " +
            TAppInfo.ID + " INT NOT NULL UNIQUE, " +
            TAppInfo.TYPE + " INT, " +
            TAppInfo.NAME + " TEXT, " +
            TAppInfo.DISCOUNTED + " BOOLEAN, " +
            TAppInfo.DISCOUNT_PERCENT + " INT, " +
            TAppInfo.ORIGINAL_PRICE + " INT, " +
            TAppInfo.FINAL_PRICE + " INT, " +
            TAppInfo.CURRENCY + " TEXT, " +
            TAppInfo.LARGE_CAPSULE_IMAGE + " TEXT, " +
            TAppInfo.SMALL_CAPSULE_IMAGE + " TEXT, " +
            TAppInfo.DISCOUNT_EXPIRATION + " INT, " +
            TAppInfo.HEADLINE + " TEXT, " +
            TAppInfo.CONTROLLER_SUPPORT + " TEXT, " +
            TAppInfo.PURCHASE_PACKAGE + " TEXT" +
            ");";

    public static final String CREATE_DEALS =
            "create table " + TDeals.TABLE + " (" +
                    TDeals._ID + " INT PRIMARY KEY AUTOINCREMENT, " +
                    TDeals.TYPE + " INT NOT NULL, " +
                    TDeals.APP_ID + " INT NOT NULL" +
                    ");";

    public static final String DROP_APP_INFO = "DROP TABLE IF EXISTS " + TAppInfo.TABLE;
    public static final String DROP_DEALS = "DROP TABLE IF EXISTS " + TDeals.TABLE;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_APP_INFO);
        db.execSQL(CREATE_DEALS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_DEALS);
        db.execSQL(DROP_APP_INFO);
        onCreate(db);
    }
}
