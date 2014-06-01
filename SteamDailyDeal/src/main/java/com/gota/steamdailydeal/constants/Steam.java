package com.gota.steamdailydeal.constants;

/**
 * Created by Gota on 2014/5/18.
 * Email: G.tianxiang@gmail.com
 */
public class Steam implements SteamAPI {

    public static String getStoreLink(int type, int id) {
       return String.format(STORE_LINK, getTypeStr(type), id);
    }

    public static String getLargePic(int type, int id) {
        return String.format(CAPSULE_LARGE, getTypeStr(type) + "s", id);
    }

    public static String getMediumPic(int type, int id) {
        return String.format(CAPSULE_MEDIUM, getTypeStr(type) + "s", id);
    }

    public static String getSmallPic(int type, int id) {
        return String.format(CAPSULE_SMALL, getTypeStr(type) + "s", id);
    }

    public static String getTypeStr(int type) {
        switch (type) {
            case TYPE_APP:
                return "app";
            case TYPE_SUB:
                return "sub";
            default:
                return "";
        }
    }

}
