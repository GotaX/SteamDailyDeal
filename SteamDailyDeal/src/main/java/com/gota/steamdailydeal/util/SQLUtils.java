package com.gota.steamdailydeal.util;

import android.text.TextUtils;

/**
 * Created by Gota on 2014/5/31.
 * Email: G.tianxiang@gmail.com
 */
public class SQLUtils {

    public static String createWhere(String where1, String where2) {
        if (TextUtils.isEmpty(where2)) {
            return where1;
        } else {
            return "(" + where1 + ") AND (" + where2 + ")";
        }
    }

}
