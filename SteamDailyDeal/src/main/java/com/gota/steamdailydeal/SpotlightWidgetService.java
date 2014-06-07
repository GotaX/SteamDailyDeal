package com.gota.steamdailydeal;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.gota.steamdailydeal.data.DataProvider;
import com.gota.steamdailydeal.data.Tables;
import com.gota.steamdailydeal.util.MyTextUtils;
import com.gota.steamdailydeal.util.UIUtils;

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

        private AppWidgetManager mAppWidgetManager;
        private String mPackageName;
        private int mAppWidgetId;
        private Cursor mCursor;
        private Context mContext;

        public FlipperRemoteViewsFactory(Context context, Intent intent) {
            this.mContext = context;
            this.mAppWidgetManager = AppWidgetManager.getInstance(context);
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
            Log.d(App.TAG, "Setup view daily deal");

            RemoteViews views = new RemoteViews(mPackageName, R.layout.deal_item);
            UIUtils.setupDealView(views, mCursor);

            int appId = mCursor.getInt(mCursor.getColumnIndex(Tables.TDeals.ID));
            String url = MyTextUtils.getStoreLink(appId);

            Intent clickIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            views.setOnClickFillInIntent(R.id.img_header, clickIntent);
            return views;
        }

        private RemoteViews setupSpotLightView() {
            Log.d(App.TAG, "Setup view spotlight");

            RemoteViews views = new RemoteViews(mPackageName, R.layout.deal_item);
            UIUtils.setupSpotlightView(views, mCursor);

            String url = mCursor.getString(mCursor.getColumnIndex(Tables.TDeals.URL));
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
