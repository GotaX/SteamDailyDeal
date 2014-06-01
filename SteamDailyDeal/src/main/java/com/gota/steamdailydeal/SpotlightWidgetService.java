package com.gota.steamdailydeal;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.gota.steamdailydeal.data.DataProvider;
import com.gota.steamdailydeal.data.Tables;
import com.gota.steamdailydeal.util.MyTextUtils;
import com.gota.steamdailydeal.util.NetUtils;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Gota on 2014/5/20.
 * Email: G.tianxiang@gmail.com
 */
public class SpotlightWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FlipperRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    static class FlipperRemoteViewsFactory implements RemoteViewsFactory {

        private static Bitmap sPicNotFound;

        private AppWidgetManager mAppWidgetManager;
        private String mPackageName;
        private int mAppWidgetId;
        private Cursor mCursor;
        private Context mContext;

        public FlipperRemoteViewsFactory(Context context, Intent intent) {
            this.mContext = context;
            this.mAppWidgetManager = AppWidgetManager.getInstance(context);
            sPicNotFound = BitmapFactory.decodeResource(
                    mContext.getResources(), R.drawable.not_found);
            mAppWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            this.mPackageName = mContext.getPackageName();
        }

        @Override
        public void onDataSetChanged() {
            Log.d(App.TAG, "service data set changed!");

            // Close old cursor, and query new
            if (mCursor != null) {
                mCursor.close();
            }
            Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, DataProvider.PATH_DEAL);
            String where = String.format("%s in (%s, %s)",
                    Tables.TDeals.CATEGORY,
                    Tables.TDeals.CAT_DAILY_DEAL,
                    Tables.TDeals.CAT_SPOTLIGHT);
            mCursor = mContext.getContentResolver().query(uri, null, where, null, null);

            // Download image and cache it
            CountDownLatch countDown = new CountDownLatch(mCursor.getCount());
            while (mCursor.moveToNext()) {
                String imageUrl = mCursor.getString(
                        mCursor.getColumnIndex(Tables.TDeals.HEADER_IMAGE));
                NetUtils.loadImageToCache(countDown, imageUrl);
            }
            try {
                countDown.await();
            } catch (InterruptedException e) {
                Log.e(App.TAG, "Interrupted when load image to cache!", e);
            }
        }

        @Override
        public void onDestroy() {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        @Override
        public int getCount() {
            return mCursor == null ? 0 :mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            mCursor.moveToPosition(position);

            int category = mCursor.getInt(mCursor.getColumnIndex(Tables.TDeals.CATEGORY));

            switch (category) {
                case Tables.TDeals.CAT_DAILY_DEAL:
                    return setupDailyDealView();
                case Tables.TDeals.CAT_SPOTLIGHT:
                    return setupSpotLightView();
                default:
                    return null;
            }
        }

        private RemoteViews setupDailyDealView() {
            RemoteViews views = new RemoteViews(mPackageName, R.layout.deal_item);

            Log.d(App.TAG, "Setup view daily deal");
            String imgHeader = mCursor.getString(
                    mCursor.getColumnIndex(Tables.TDeals.HEADER_IMAGE));
            String name = mCursor.getString(
                    mCursor.getColumnIndex(Tables.TDeals.NAME));
            int originalPrice = mCursor.getInt(
                    mCursor.getColumnIndex(Tables.TDeals.ORIGINAL_PRICE));
            int price = mCursor.getInt(
                    mCursor.getColumnIndex(Tables.TDeals.FINAL_PRICE));
            int appId = mCursor.getInt(
                    mCursor.getColumnIndex(Tables.TDeals.ID));
            String currency = mCursor.getString(
                    mCursor.getColumnIndex(Tables.TDeals.CURRENCY));
            String discountPercent = mCursor.getString(
                    mCursor.getColumnIndex(Tables.TDeals.DISCOUNT_PERCENT));

            CharSequence sOriginalPrice = MyTextUtils.getCurrency(
                    originalPrice, currency);
            sOriginalPrice = MyTextUtils.strikethrough(sOriginalPrice);
            CharSequence sPrice = MyTextUtils.getCurrency(price, currency);
            CharSequence sDiscountPercent = MyTextUtils.getDiscount(discountPercent);
            String url = MyTextUtils.getStoreLink(appId);

            NetUtils.loadNetImage(views, imgHeader);

            views.setTextViewText(R.id.tv_name, name);
            views.setTextViewText(R.id.tv_original_price, sOriginalPrice);
            views.setTextViewText(R.id.tv_price, sPrice);
            views.setTextViewText(R.id.tv_discount_percent, sDiscountPercent);

            Intent clickIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            views.setOnClickFillInIntent(R.id.img_header, clickIntent);

            return views;
        }

        private RemoteViews setupSpotLightView() {
            Log.d(App.TAG, "Setup view spotlight");
            final RemoteViews views = new RemoteViews(mPackageName, R.layout.deal_item);

            String imgHeader = mCursor.getString(
                    mCursor.getColumnIndex(Tables.TDeals.HEADER_IMAGE));
            String name = mCursor.getString(
                    mCursor.getColumnIndex(Tables.TDeals.NAME));
            String body = mCursor.getString(
                    mCursor.getColumnIndex(Tables.TDeals.BODY));
            String url = mCursor.getString(
                    mCursor.getColumnIndex(Tables.TDeals.URL));

            NetUtils.loadNetImage(views, imgHeader);
            views.setTextViewText(R.id.tv_name, name);
            views.setViewVisibility(R.id.area_price, View.GONE);

            Intent clickIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            views.setOnClickFillInIntent(R.id.img_header, clickIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(mPackageName, R.layout.view_loading);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
