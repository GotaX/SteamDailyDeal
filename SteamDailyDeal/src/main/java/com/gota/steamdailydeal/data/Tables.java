package com.gota.steamdailydeal.data;

import android.provider.BaseColumns;

/**
 * Created by Gota on 2014/5/20.
 * Email: G.tianxiang@gmail.com
 */
public class Tables {

    public static interface SQL {
        public static final String DEALS_JOIN_APP_INFO =
                TDeals.TABLE + " inner join " + TAppInfo.TABLE + " on " +
                TDeals.TABLE + "." + TDeals.APP_ID + " = " +
                TAppInfo.TABLE + "." + TAppInfo.ID;

        public static final String[] DEALS_JOIN_APP_INFO_PROJECTION = {
                TAppInfo.ID, TAppInfo.TYPE, TAppInfo.NAME, TAppInfo.DISCOUNTED,
                TAppInfo.DISCOUNT_PERCENT, TAppInfo.ORIGINAL_PRICE, TAppInfo.FINAL_PRICE,
                TAppInfo.CURRENCY, TAppInfo.LARGE_CAPSULE_IMAGE, TAppInfo.SMALL_CAPSULE_IMAGE,
                TAppInfo.DISCOUNT_EXPIRATION, TAppInfo.HEADLINE, TAppInfo.CONTROLLER_SUPPORT,
                TAppInfo.PURCHASE_PACKAGE
        };
    }

    public static interface TDeals extends BaseColumns {
        public static final String TABLE = "deals";

        public static final String TYPE = "deal_type";
        public static final String APP_ID = "app_id";

        public static final int TYPE_DAILY_DEAL = 1;
        public static final int TYPE_WEEK_LONG_DEAL = 2;
        public static final int TYPE_WEDNESDAY_DEAL = 3;
        public static final int TYPE_SOPTLIGHT = 4;
    }

    public static interface TAppInfo extends BaseColumns {
        public static final String TABLE = "app_info";

        public static final String ID = "app_id";
        public static final String TYPE = "type";
        public static final String NAME = "app_name";
        public static final String DISCOUNTED = "discounted";
        public static final String DISCOUNT_PERCENT = "discount_percent";
        public static final String ORIGINAL_PRICE = "original_price";
        public static final String FINAL_PRICE = "final_price";
        public static final String CURRENCY = "currency";
        public static final String LARGE_CAPSULE_IMAGE = "large_capsule_image";
        public static final String SMALL_CAPSULE_IMAGE = "small_capsule_image";
        public static final String DISCOUNT_EXPIRATION = "discount_expiration";
        public static final String HEADLINE = "headline";

        public static final String CONTROLLER_SUPPORT = "controller_support";
        public static final String PURCHASE_PACKAGE = "purchase_package";
    }

    public static interface TSpotlight extends BaseColumns {
        public static final String TABLE = "spotlight";

        public static final String NAME = "name";
        public static final String HEADER_IMAGE = "header_image";
        public static final String BODY = "body";
        public static final String URL = "url";
    }

    public static interface TFeatured extends BaseColumns {
        public static final String TABLE = "featured";

        public static final String ID = "cat_id";
        public static final String NAME = "cat_name";
        public static final String ITEMS = "items";
    }

}
