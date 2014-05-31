package com.gota.steamdailydeal.data;

import android.provider.BaseColumns;

/**
 * Created by Gota on 2014/5/20.
 * Email: G.tianxiang@gmail.com
 */
public class Tables {

    public static interface SQL {
        String[] PROJECTION_DAILY_DEAL = {
                TDeals._ID, TDeals.ID, TDeals.TYPE, TDeals.DISCOUNTED,
                TDeals.CURRENCY, TDeals.ORIGINAL_PRICE, TDeals.FINAL_PRICE,
                TDeals.DISCOUNT_PERCENT, TDeals.NAME, TDeals.HEADER_IMAGE,
                TDeals.PURCHASE_PACKAGE
        };
        String[] PROJECTION_SPOTLIGHT = {
                TDeals._ID, TDeals.NAME, TDeals.HEADER_IMAGE, TDeals.BODY,
                TDeals.URL
        };
        String[] PROJECTION_WEEK_LONG = {
                // TODO: Write week long projection
        };
    }

    public static interface TDeals extends BaseColumns {
        String TABLE = "deal";

        String ID = "app_id";
        String TYPE = "type";
        String NAME = "app_name";
        String DISCOUNTED = "discounted";
        String DISCOUNT_PERCENT = "discount_percent";
        String ORIGINAL_PRICE = "original_price";
        String FINAL_PRICE = "final_price";
        String CURRENCY = "currency";
        String LARGE_CAPSULE_IMAGE = "large_capsule_image";
        String SMALL_CAPSULE_IMAGE = "small_capsule_image";
        String DISCOUNT_EXPIRATION = "discount_expiration";
        String HEADLINE = "headline";

        String CONTROLLER_SUPPORT = "controller_support";
        String PURCHASE_PACKAGE = "purchase_package";

        String HEADER_IMAGE = "header_image";
        String BODY = "body";
        String URL = "url";

        String CATEGORY = "category";

        int CAT_DAILY_DEAL = 1;
        int CAT_SPOTLIGHT = 2;
        int CAT_WEEK_LONG_DEAL = 3;
    }

}
