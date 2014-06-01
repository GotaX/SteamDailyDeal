package com.gota.steamdailydeal.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.gota.steamdailydeal.App;
import com.gota.steamdailydeal.DailyDealWidget;
import com.gota.steamdailydeal.R;
import com.gota.steamdailydeal.SpotlightWidgetService;
import com.gota.steamdailydeal.WeekLongDealsWidgetService;
import com.gota.steamdailydeal.constants.Steam;
import com.gota.steamdailydeal.data.DataProvider;
import com.gota.steamdailydeal.data.Tables;
import com.gota.steamdailydeal.util.MyTextUtils;

/**
 * Created by Gota on 2014/6/1.
 * Email: G.tianxiang@gmail.com
 */
public class LayoutBuilder {

    private Context mContext;
    private AppWidgetManager mAppWidgetManager;

    public LayoutBuilder(Context context) {
        this.mContext = context;
        this.mAppWidgetManager = AppWidgetManager.getInstance(context);
    }

    public RemoteViews buildSmallLayout(final int appWidgetId) {
        ContentResolver cr = mContext.getContentResolver();
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, DataProvider.PATH_DAILY_DEAL);
        Cursor cursor = cr.query(uri, Tables.SQL.PROJECTION_DAILY_DEAL_SMALL, null, null, null);

        if (cursor.moveToFirst()) {
            final RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.app_small_size);

            int id = cursor.getInt(cursor.getColumnIndex(Tables.TDeals.ID));
            int type = cursor.getInt(cursor.getColumnIndex(Tables.TDeals.TYPE));
            String headerImage = Steam.getMediumPic(type, id);
            String storeLink = Steam.getStoreLink(type, id);
            String currency = cursor.getString(cursor.getColumnIndex(Tables.TDeals.CURRENCY));
            CharSequence name = cursor.getString(cursor.getColumnIndex(Tables.TDeals.NAME));
            CharSequence discountPercent = MyTextUtils.getDiscount(
                    cursor.getString(cursor.getColumnIndex(Tables.TDeals.DISCOUNT_PERCENT)));
            int op = cursor.getInt(cursor.getColumnIndex(Tables.TDeals.ORIGINAL_PRICE));
            CharSequence originalPrice = MyTextUtils.strikethrough(MyTextUtils.getCurrency(op, currency));
            int fp = cursor.getInt(cursor.getColumnIndex(Tables.TDeals.FINAL_PRICE));
            CharSequence finalPrice = MyTextUtils.getCurrency(fp, currency);

            App.imgLoader.get(headerImage, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    views.setImageViewBitmap(R.id.img_header, imageContainer.getBitmap());
                    mAppWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
                }
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(App.TAG, "Error on request small image!", volleyError);
                    views.setImageViewResource(R.id.img_header, R.drawable.not_found);
                    mAppWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
                }
            });
            views.setTextViewText(R.id.tv_name, name);
            views.setTextViewText(R.id.tv_discount_percent, discountPercent);
            views.setTextViewText(R.id.tv_original_price, originalPrice);
            views.setTextViewText(R.id.tv_price, finalPrice);

            views.setOnClickPendingIntent(R.id.btn_refresh, createRefreshPendingIntent());
            views.setOnClickPendingIntent(R.id.img_header, createHeaderImagePendingIntent(storeLink));
            return views;
        } else {
            // TODO: Return empty view
            return null;
        }
    }

    public RemoteViews buildMediumLayout(final int appWidgetId) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.app_normal_size);
        Intent intent = new Intent(mContext, SpotlightWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.vf_main, intent);

        views.setEmptyView(R.id.vf_main, R.id.empty);

        // Init refresh button
        Intent refreshIntent = new Intent(mContext, DailyDealWidget.class);
        refreshIntent.setAction(DailyDealWidget.ACTION_REFRESH);
        PendingIntent piRefresh = PendingIntent.getBroadcast(
                mContext, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btn_refresh, piRefresh);

        // Init onclick image
        Intent ivf = new Intent(Intent.ACTION_VIEW);
        PendingIntent pi = PendingIntent.getActivity(
                mContext, 0, ivf, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.vf_main, pi);

        mAppWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.vf_main);

        return views;
    }

    public RemoteViews buildLargeLayout(final int appWidgetId) {
        final RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.app_large_size);


        // Setup daily deal view
        ContentResolver cr = mContext.getContentResolver();
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, DataProvider.PATH_DAILY_DEAL);
        Cursor cursor = cr.query(uri, Tables.SQL.PROJECTION_DAILY_DEAL_SMALL, null, null, null);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(Tables.TDeals.ID));
            int type = cursor.getInt(cursor.getColumnIndex(Tables.TDeals.TYPE));
            String headerImage = Steam.getMediumPic(type, id);
            String storeLink = Steam.getStoreLink(type, id);
            String currency = cursor.getString(cursor.getColumnIndex(Tables.TDeals.CURRENCY));
            CharSequence name = cursor.getString(cursor.getColumnIndex(Tables.TDeals.NAME));
            CharSequence discountPercent = MyTextUtils.getDiscount(
                    cursor.getString(cursor.getColumnIndex(Tables.TDeals.DISCOUNT_PERCENT)));
            int op = cursor.getInt(cursor.getColumnIndex(Tables.TDeals.ORIGINAL_PRICE));
            CharSequence originalPrice = MyTextUtils.strikethrough(MyTextUtils.getCurrency(op, currency));
            int fp = cursor.getInt(cursor.getColumnIndex(Tables.TDeals.FINAL_PRICE));
            CharSequence finalPrice = MyTextUtils.getCurrency(fp, currency);

            App.imgLoader.get(headerImage, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    views.setImageViewBitmap(R.id.img_header, imageContainer.getBitmap());
                    mAppWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
                }
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(App.TAG, "Error on request small image!", volleyError);
                    views.setImageViewResource(R.id.img_header, R.drawable.not_found);
                    mAppWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
                }
            });
            views.setTextViewText(R.id.tv_name, name);
            views.setTextViewText(R.id.tv_discount_percent, discountPercent);
            views.setTextViewText(R.id.tv_original_price, originalPrice);
            views.setTextViewText(R.id.tv_price, finalPrice);

            views.setOnClickPendingIntent(R.id.btn_refresh, createRefreshPendingIntent());
            views.setOnClickPendingIntent(R.id.img_header, createHeaderImagePendingIntent(storeLink));
        }


        // Setup list
        Intent intent = new Intent(mContext, WeekLongDealsWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.lv_week_long_deals, intent);

        views.setEmptyView(R.id.lv_week_long_deals, R.id.empty);

        // Init onclick item
        Intent ivf = new Intent(Intent.ACTION_VIEW);
        PendingIntent pi = PendingIntent.getActivity(
                mContext, 1, ivf, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.lv_week_long_deals, pi);

        mAppWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_week_long_deals);

        return views;
    }

    private PendingIntent createRefreshPendingIntent() {
        Intent refreshIntent = new Intent(mContext, DailyDealWidget.class);
        refreshIntent.setAction(DailyDealWidget.ACTION_REFRESH);
        return PendingIntent.getBroadcast(
                mContext, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createHeaderImagePendingIntent(String url) {
        Intent ivf = new Intent(Intent.ACTION_VIEW);
        ivf.setData(Uri.parse(url));
        return PendingIntent.getActivity(
                mContext, 0, ivf, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
