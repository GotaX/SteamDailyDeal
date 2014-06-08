package com.gota.steamdailydeal;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.gota.steamdailydeal.activity.DetailDialogActivity;
import com.gota.steamdailydeal.data.DataProvider;
import com.gota.steamdailydeal.data.Tables;
import com.gota.steamdailydeal.util.UIUtils;

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
        private String mPackageName;
        private Cursor mCursor;

        public WeekLongDealsRemotesViewFactory(Context context, Intent intent) {
            this.mContext = context;
            this.mPackageName = context.getPackageName();
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
            mCursor.moveToPosition(position);

            RemoteViews views = new RemoteViews(mPackageName, R.layout.week_long_deal_item);
            UIUtils.setupDealView(views, mCursor);

            int type = mCursor.getInt(mCursor.getColumnIndex(Tables.TDeals.TYPE));
            int appId = mCursor.getInt(mCursor.getColumnIndex(Tables.TDeals.ID));
            String name = mCursor.getString(mCursor.getColumnIndex(Tables.TDeals.NAME));
            Intent clickIntent = new Intent();
            clickIntent.putExtra(DetailDialogActivity.KEY_APP_TYPE, type);
            clickIntent.putExtra(DetailDialogActivity.KEY_APP_ID, appId);
            clickIntent.putExtra(DetailDialogActivity.KEY_APP_NAME, name);
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
