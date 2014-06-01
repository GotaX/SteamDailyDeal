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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Gota on 2014/5/20.
 * Email: G.tianxiang@gmail.com
 */
public class MyWidgetService extends RemoteViewsService {

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
                loadImageToCache(countDown, imageUrl);
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
            Log.d(App.TAG, "Get view at " + position);

            mCursor.moveToPosition(position);

            int categoryIndex = mCursor.getColumnIndex(Tables.TDeals.CATEGORY);
            int category = mCursor.getInt(categoryIndex);

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
            RemoteViews views = new RemoteViews(mPackageName, R.layout.flip_item);

            Log.d(App.TAG, "Setup view daily deal");
            String imgHeader = mCursor.getString(
                    mCursor.getColumnIndex(Tables.TDeals.HEADER_IMAGE));
            String name = mCursor.getString(
                    mCursor.getColumnIndex(Tables.TDeals.NAME));
            int originalPrice = mCursor.getInt(
                    mCursor.getColumnIndex(Tables.TDeals.ORIGINAL_PRICE));
            int price = mCursor.getInt(
                    mCursor.getColumnIndex(Tables.TDeals.FINAL_PRICE));
            String appId = mCursor.getString(
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

            loadNetImage(views, imgHeader);

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
            final RemoteViews views = new RemoteViews(mPackageName, R.layout.flip_item);

            String imgHeader = mCursor.getString(
                    mCursor.getColumnIndex(Tables.TDeals.HEADER_IMAGE));
            String name = mCursor.getString(
                    mCursor.getColumnIndex(Tables.TDeals.NAME));
            String body = mCursor.getString(
                    mCursor.getColumnIndex(Tables.TDeals.BODY));
            String url = mCursor.getString(
                    mCursor.getColumnIndex(Tables.TDeals.URL));

            loadNetImage(views, imgHeader);
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

        private void loadNetImage(RemoteViews views, String url) {
            Bitmap bitmap = App.cache.getBitmap(url);
            if (bitmap == null) {
                views.setImageViewResource(R.id.img_header, R.drawable.not_found);
            } else {
                views.setImageViewBitmap(R.id.img_header, bitmap);
            }
        }

        private void loadImageToCache(final CountDownLatch latch, final String url) {
            if (App.cache.isCached(url)) {
                Log.d(App.TAG, "Image cached: " + url);
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d(App.TAG, "Start download image: " + url);
                        URLConnection connection = new URL(url).openConnection();
                        InputStream in = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(in);
                        in.close();

                        Log.d(App.TAG, "Add image to cache!" + url);
                        App.cache.putBitmap(url, bitmap);
                    } catch (IOException e) {
                        Log.e(App.TAG, "Error on download image!", e);
                    } finally {
                        latch.countDown();
                    }
                }
            }).start();
        }
    }
}
