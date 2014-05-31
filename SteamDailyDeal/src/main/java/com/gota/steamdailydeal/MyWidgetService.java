package com.gota.steamdailydeal;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.RequestFuture;
import com.gota.steamdailydeal.data.DataProvider;

/**
 * Created by Gota on 2014/5/20.
 * Email: G.tianxiang@gmail.com
 */
public class MyWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FliperRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    static class FliperRemoteViewsFactory implements RemoteViewsFactory {

        private static Bitmap sPicNotFound;

        private String mPackageName;
        private int mAppWidgetId;
        private Cursor mCursor;
        private Context mContext;

        public FliperRemoteViewsFactory(Context context, Intent intent) {
            this.mContext = context;
            sPicNotFound = BitmapFactory.decodeResource(
                    mContext.getResources(), R.drawable.not_found);
            mAppWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            this.mPackageName = App.instance.getPackageName();
        }

        @Override
        public void onDataSetChanged() {
            Log.d(App.TAG, "service data set changed!");
            if (mCursor != null) {
                mCursor.close();
            }
            mCursor = mContext.getContentResolver().query(
                    DataProvider.CONTENT_URI, null, null, null, null);
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
            RemoteViews views = new RemoteViews(mPackageName, R.layout.flip_item);
            mCursor.moveToPosition(position);
/*

            int fidIndex = mCursor.getColumnIndex(Tables.TFeatured.ID);
            String fid = mCursor.getString(fidIndex);
*/

            /*if (StorefrontAPI.CAT_SPOTLIGHT.equals(fid)) {
                Log.d(App.TAG, "Setup view spotlight");
                String imgHeader = mCursor.getString(
                        mCursor.getColumnIndex(Tables.TAppInfo.HEADER_IMAGE));
                String name = mCursor.getString(
                        mCursor.getColumnIndex(Tables.TAppInfo.NAME));
                String body = mCursor.getString(
                        mCursor.getColumnIndex(Tables.TAppInfo.BODY));
                String url = mCursor.getString(
                        mCursor.getColumnIndex(Tables.TAppInfo.URL));
                Bitmap bitmapHeader = getNetImage(imgHeader);

                views.setImageViewBitmap(R.id.img_header, bitmapHeader);
                views.setTextViewText(R.id.tv_name, name);
                views.setViewVisibility(R.id.area_price, View.GONE);

                Intent clickIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                views.setOnClickFillInIntent(R.id.img_header, clickIntent);
            } else if(StorefrontAPI.CAT_DAILYDEAL.equals(fid)) {
                Log.d(App.TAG, "Setup view dailydeal");
                String imgHeader = mCursor.getString(
                        mCursor.getColumnIndex(Tables.TAppInfo.HEADER_IMAGE));
                String name = mCursor.getString(
                        mCursor.getColumnIndex(Tables.TAppInfo.NAME));
                int originalPrice = mCursor.getInt(
                        mCursor.getColumnIndex(Tables.TAppInfo.ORIGINAL_PRICE));
                int price = mCursor.getInt(
                        mCursor.getColumnIndex(Tables.TAppInfo.FINAL_PRICE));
                String appId = mCursor.getString(
                        mCursor.getColumnIndex(Tables.TAppInfo.ID));
                String currency = mCursor.getString(
                        mCursor.getColumnIndex(Tables.TAppInfo.CURRENCY));
                String discountPercent = mCursor.getString(
                        mCursor.getColumnIndex(Tables.TAppInfo.DISCOUNT_PERCENT));

                CharSequence sOriginalPrice = MyTextUtils.getCurrency(
                        originalPrice, currency);
                sOriginalPrice = MyTextUtils.strikethrough(sOriginalPrice);
                CharSequence sPrice = MyTextUtils.getCurrency(price, currency);
                CharSequence sDiscountPercent = MyTextUtils.getDiscount(discountPercent);
                String url = MyTextUtils.getStoreLink(appId);
                Bitmap bitmapHeader = getNetImage(imgHeader);

                views.setImageViewBitmap(R.id.img_header, bitmapHeader);
                views.setTextViewText(R.id.tv_name, name);
                views.setTextViewText(R.id.tv_original_price, sOriginalPrice);
                views.setTextViewText(R.id.tv_price, sPrice);
                views.setTextViewText(R.id.tv_discount_percent, sDiscountPercent);

                Intent clickIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                views.setOnClickFillInIntent(R.id.img_header, clickIntent);
            }*/

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

        private Bitmap getNetImage(String url) {
            RequestFuture<Bitmap> future = RequestFuture.newFuture();
            ImageRequest imageRequest = new ImageRequest(
                    url, future, 400, 400, Bitmap.Config.ALPHA_8, future);
            App.queue.add(imageRequest);
            try {
                return future.get();
            } catch (Exception e) {
                return sPicNotFound;
            }
        }
    }
}
