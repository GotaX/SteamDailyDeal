package com.gota.steamdailydeal;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.gota.steamdailydeal.constants.Steam;
import com.gota.steamdailydeal.data.DataProvider;
import com.gota.steamdailydeal.data.Tables;
import com.gota.steamdailydeal.util.MyTextUtils;
import com.gota.steamdailydeal.util.NetUtils;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Gota on 2014/6/1.
 * Email: G.tianxiang@gmail.com
 */
public class WeekLongDealsWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WeekLongDealsRemotesViewFactory(getApplicationContext(), intent);
    }

    private class WeekLongDealsRemotesViewFactory implements RemoteViewsFactory {

        private Context mContext;
        private int mAppWidgetId;
        private AppWidgetManager mAppWidgetManager;
        private String mPackageName;
        private Cursor mCursor;

        public WeekLongDealsRemotesViewFactory(Context context, Intent intent) {
            this.mContext = context;
            this.mPackageName = context.getPackageName();
            this.mAppWidgetManager = AppWidgetManager.getInstance(context);
            mAppWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            Log.d(App.TAG, "Week long deal remote view factory created");
        }

        @Override
        public void onDataSetChanged() {
            Log.d(App.TAG, "Week long deals service data set changed!");

            // Close old cursor, and query new
            if (mCursor != null) {
                mCursor.close();
            }
            Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, DataProvider.PATH_WEEK_LONG_DEAL);
            mCursor = mContext.getContentResolver().query(uri, null, null, null, null);
            Log.d(App.TAG, "Week long deal count: " + mCursor.getCount());

            // Download image and cache it
            CountDownLatch countDown = new CountDownLatch(mCursor.getCount());
            while (mCursor.moveToNext()) {
                int id = mCursor.getInt(mCursor.getColumnIndex(Tables.TDeals.ID));
                int type = mCursor.getInt(mCursor.getColumnIndex(Tables.TDeals.TYPE));
                String imageUrl = Steam.getSmallPic(type, id);
                NetUtils.loadImageToCache(countDown, imageUrl);
            }
            try {
                countDown.await();
                Log.d(App.TAG, "Loaded " + mCursor.getCount() + " images to cache!");
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
            return mCursor == null ? 0 : mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Log.d(App.TAG, "Week long deal item "+ position);
            mCursor.moveToPosition(position);

            int appId = mCursor.getInt(mCursor.getColumnIndex(Tables.TDeals.ID));
            int type = mCursor.getInt(mCursor.getColumnIndex(Tables.TDeals.TYPE));
            String imgHeader = Steam.getSmallPic(type, appId);
            String name = mCursor.getString(mCursor.getColumnIndex(Tables.TDeals.NAME));

            int originalPrice = mCursor.getInt(
                    mCursor.getColumnIndex(Tables.TDeals.ORIGINAL_PRICE));
            int price = mCursor.getInt(
                    mCursor.getColumnIndex(Tables.TDeals.FINAL_PRICE));
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

            RemoteViews views = new RemoteViews(mPackageName, R.layout.week_long_deal_item);

            NetUtils.loadImageFromCache(views, imgHeader);
            views.setTextViewText(R.id.tv_name, name);
            views.setTextViewText(R.id.tv_original_price, sOriginalPrice);
            views.setTextViewText(R.id.tv_price, sPrice);
            views.setTextViewText(R.id.tv_discount_percent, sDiscountPercent);

            Intent clickIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            views.setOnClickFillInIntent(R.id.ll_deal_item, clickIntent);

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
