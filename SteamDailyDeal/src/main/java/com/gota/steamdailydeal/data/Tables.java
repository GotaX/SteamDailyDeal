package com.gota.steamdailydeal.data;

import android.provider.BaseColumns;

/**
 * Created by Gota on 2014/5/20.
 * Email: G.tianxiang@gmail.com
 */
public class Tables {

    public static interface TAppInfo extends BaseColumns {
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

        public static final String HEADER_IMAGE = "header_image";
        public static final String BODY = "body";
        public static final String URL = "url";
    }

    public static interface TFeatured extends BaseColumns {
        public static final String ID = "cat_id";
        public static final String NAME = "cat_name";
        public static final String ITEMS = "items";
    }

}
