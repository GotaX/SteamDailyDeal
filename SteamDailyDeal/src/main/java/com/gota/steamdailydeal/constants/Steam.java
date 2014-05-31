package com.gota.steamdailydeal.constants;

/**
 * Created by Gota on 2014/5/18.
 * Email: G.tianxiang@gmail.com
 */
public class Steam implements SteamAPI {

    public static String getStoreLink(int id) {
       return String.format(STORE_LINK, id);
    }

    public static String getLargePic(int id) {
        return String.format(CAPSULE_LARGE, id);
    }

    public static String getMediumPic(int id) {
        return String.format(CAPSULE_MEDIUM, id);
    }

    public static String getSmallPic(int id) {
        return String.format(CAPSULE_SMALL, id);
    }

}
