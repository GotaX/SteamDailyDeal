package com.gota.steamdailydeal.util;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.RemoteViews;

import com.gota.steamdailydeal.App;
import com.gota.steamdailydeal.DailyDealWidget;
import com.gota.steamdailydeal.R;
import com.gota.steamdailydeal.constants.Steam;
import com.gota.steamdailydeal.data.Tables;

/**
 * Created by Gota on 2014/6/8.
 * Email: G.tianxiang@gmail.com
 */
public class UIUtils {

    public static void setupDealView(RemoteViews views, Cursor cursor) {
        // Prepare text
        int id = cursor.getInt(cursor.getColumnIndex(Tables.TDeals.ID));
        int type = cursor.getInt(cursor.getColumnIndex(Tables.TDeals.TYPE));
        String currency = cursor.getString(cursor.getColumnIndex(Tables.TDeals.CURRENCY));
        String name = cursor.getString(cursor.getColumnIndex(Tables.TDeals.NAME));
        int op = cursor.getInt(cursor.getColumnIndex(Tables.TDeals.ORIGINAL_PRICE));
        int fp = cursor.getInt(cursor.getColumnIndex(Tables.TDeals.FINAL_PRICE));
        String dp = cursor.getString(cursor.getColumnIndex(Tables.TDeals.DISCOUNT_PERCENT));

        CharSequence originalPrice = MyTextUtils.strikethrough(MyTextUtils.getCurrency(op, currency));
        CharSequence finalPrice = MyTextUtils.getCurrency(fp, currency);
        CharSequence discountPercent = MyTextUtils.getDiscount(dp);

        // Prepare image
        int colImage = cursor.getColumnIndex(Tables.TDeals.HEADER_IMAGE);
        String headerImage = colImage < 0 ?
                Steam.getMediumPic(type, id) :
                cursor.getString(colImage);
        Bitmap bitmap = App.cache.getBitmap(headerImage);
        views.setImageViewBitmap(R.id.img_header, bitmap);
        views.setTextViewText(R.id.tv_name, name);
        views.setTextViewText(R.id.tv_discount_percent, discountPercent);
        views.setTextViewText(R.id.tv_original_price, originalPrice);
        views.setTextViewText(R.id.tv_price, finalPrice);
    }

    public static void setupSpotlightView(RemoteViews views, Cursor cursor) {
        String imgHeader = cursor.getString(cursor.getColumnIndex(Tables.TDeals.HEADER_IMAGE));
        String name = cursor.getString(cursor.getColumnIndex(Tables.TDeals.NAME));
        Bitmap bitmap = App.cache.getBitmap(imgHeader);

        views.setImageViewBitmap(R.id.img_header, bitmap);
        views.setTextViewText(R.id.tv_name, name);
        views.setViewVisibility(R.id.area_price, View.GONE);
    }

    public static DailyDealWidget.Size calculateSize(int height) {
        int cell = (height + 2) / 72;
        if (cell <= 1) {
            return DailyDealWidget.Size.SMALL;
        } else if (cell <= 3) {
            return DailyDealWidget.Size.MEDIUM;
        } else {
            return DailyDealWidget.Size.LARGE;
        }
    }
}
