package com.gota.steamdailydeal.constants;

/**
 * Created by Gota on 2014/5/18.
 * Email: G.tianxiang@gmail.com
 */
public class Pref {
    public static final String REFRESH_HOUER = "pref_refresh_hour";
    public static final String REFRESH_MINITUE = "pref_refresh_minitue";

    public static final String getSizeKey(int widgetId) {
        return widgetId + "_size";
    }
}
